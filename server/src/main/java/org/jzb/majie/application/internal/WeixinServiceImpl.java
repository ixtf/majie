package org.jzb.majie.application.internal;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.WeixinService;
import org.jzb.majie.domain.Operator;
import org.jzb.majie.domain.WeixinOperator;
import org.jzb.weixin.mp.MpClient;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class WeixinServiceImpl implements WeixinService {
    private final MpClient mpClient;
    private final Jmongo jmongo;

    @Inject
    private WeixinServiceImpl(MpClient mpClient, Jmongo jmongo) {
        this.mpClient = mpClient;
        this.jmongo = jmongo;
    }

    @Override
    public void msgPushed(String body) {
        Mono.fromCallable(() -> new MsgPushed(body)).flatMap(msgPushed -> {
            if (msgPushed.isEvt_subscribe()) {
                return Flux.merge(
                        handleSubscribe(msgPushed),
                        helpMsg(msgPushed.getFromUserName()),
                        handleInvite(msgPushed)
                ).then();
            } else if (msgPushed.isEvt_unsubscribe()) {
                return handleUnSubscribe(msgPushed);
            } else if (msgPushed.isEvt_SCAN()) {
                return handleInvite(msgPushed);
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.elastic()).doOnError(err -> log.error("", err)).subscribe();
    }

    private Mono<WeixinOperator> handleSubscribe(MsgPushed msgPushed) {
        return Mono.fromCallable(() -> mpClient.unionInfo(msgPushed.getFromUserName()).call()).flatMap(res -> {
            final String openid = res.openid();
            final Mono<WeixinOperator> byOpenid = jmongo.find(WeixinOperator.class, openid);
            final String unionid = res.unionid();
            final Mono<WeixinOperator> byUnionid = J.nonBlank(unionid) ? jmongo.find(WeixinOperator.class, eq("unionid", unionid)) : Mono.empty();

            final MongoUnitOfWork uow = jmongo.uow();
            return byOpenid.switchIfEmpty(byUnionid).doOnNext(weixinOperator -> {
                final Operator operator = weixinOperator.getOperator();
                if (operator.isDeleted()) {
                    operator.setDeleted(false);
                    uow.registerDirty(operator);
                }
            }).switchIfEmpty(Mono.fromCallable(() -> {
                final Operator operator = new Operator();
                uow.registerNew(operator);
                final WeixinOperator weixinOperator = new WeixinOperator();
                uow.registerNew(weixinOperator);

                weixinOperator.setUnionInfo(res);
                weixinOperator.setOperator(operator);
                operator.setName(weixinOperator.getNickname());
                return weixinOperator;
            })).map(weixinOperator -> {
                uow.commit();
                return weixinOperator;
            });
        });
    }

    private Mono<Void> helpMsg(String fromUserName) {
        return Mono.empty();
    }

    private Mono<Void> handleInvite(MsgPushed msgPushed) {
        final String ticket = msgPushed.getTicket();
        if (J.isBlank(ticket)) {
            return Mono.empty();
        }
        return handleSubscribe(msgPushed).map(WeixinOperator::getOperator).then();
    }

    public Mono<WeixinOperator> handleUnSubscribe(MsgPushed msgPushed) {
        return jmongo.find(WeixinOperator.class, msgPushed.getFromUserName()).flatMap(weixinOperator -> {
            final Operator operator = weixinOperator.getOperator();
            operator.setDeleted(true);
            final MongoUnitOfWork uow = jmongo.uow();
            uow.registerDirty(operator);
            return uow.rxCommit().thenReturn(weixinOperator);
        });
    }
}
