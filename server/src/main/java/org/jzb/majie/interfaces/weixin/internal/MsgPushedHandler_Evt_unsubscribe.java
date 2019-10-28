package org.jzb.majie.interfaces.weixin.internal;

import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jzb.majie.domain.WeixinOperator;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author jzb 2019-10-27
 */
@Singleton
public class MsgPushedHandler_Evt_unsubscribe extends MsgPushedHandler {

    @Inject
    private MsgPushedHandler_Evt_unsubscribe() {
    }

    @Override
    Mono<WeixinOperator> handle(MsgPushed msgPushed) {
        return jmongo.find(WeixinOperator.class, msgPushed.getFromUserName()).flatMap(weixinOperator -> Optional.ofNullable(weixinOperator)
                .map(WeixinOperator::getOperator)
                .map(operator -> {
                    operator.setDeleted(true);
                    final MongoUnitOfWork uow = jmongo.uow();
                    uow.registerDirty(operator);
                    return uow.rxCommit().thenReturn(weixinOperator);
                })
                .orElseGet(Mono::empty));
    }

    @Override
    boolean support(MsgPushed msgPushed) {
        return msgPushed.isEvt_unsubscribe();
    }
}
