package org.jzb.majie.interfaces.weixin.internal;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import org.jzb.majie.MajieModule;
import org.jzb.majie.domain.Operator;
import org.jzb.majie.domain.WeixinOperator;
import org.jzb.weixin.mp.MpClient;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author jzb 2019-10-26
 */
public abstract class MsgPushedHandler {
    protected final MpClient mpClient;
    protected final Jmongo jmongo;

    protected MsgPushedHandler() {
        mpClient = MajieModule.getInstance(MpClient.class);
        jmongo = MajieModule.getInstance(Jmongo.class);
    }

    abstract Mono handle(MsgPushed msgPushed);

    abstract boolean support(MsgPushed msgPushed);

    protected Mono<WeixinOperator> ensureWeixinOperator(MsgPushed msgPushed) {
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
            })).flatMap(it -> uow.rxCommit().thenReturn(it));
        });
    }
}
