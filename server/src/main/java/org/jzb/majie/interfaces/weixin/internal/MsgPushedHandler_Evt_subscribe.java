package org.jzb.majie.interfaces.weixin.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jzb.majie.MajieModule;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2019-10-27
 */
@Singleton
public class MsgPushedHandler_Evt_subscribe extends MsgPushedHandler {

    @Inject
    private MsgPushedHandler_Evt_subscribe() {
    }

    @Override
    Mono<Void> handle(MsgPushed msgPushed) {
        return ensureWeixinOperator(msgPushed)
                .flatMap(it -> helpMsg(msgPushed))
                .flatMap(it -> {
                    final MsgPushedHandler_Evt_SCAN evt_scan = MajieModule.getInstance(MsgPushedHandler_Evt_SCAN.class);
                    return evt_scan.handle(msgPushed);
                });
    }

    private Mono<Void> helpMsg(MsgPushed msgPushed) {
        return Mono.empty();
    }

    @Override
    boolean support(MsgPushed msgPushed) {
        return msgPushed.isEvt_subscribe();
    }
}
