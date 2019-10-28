package org.jzb.majie.interfaces.weixin.internal;

import com.github.ixtf.japp.core.J;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jzb.majie.Util;
import org.jzb.majie.interfaces.weixin.internal.WX_QR_SCENE_ID.WX_QR_SCENE_ID_Handler;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;

/**
 * @author jzb 2019-10-27
 */
@Singleton
public class MsgPushedHandler_Evt_SCAN extends MsgPushedHandler {
    private final Collection<WX_QR_SCENE_ID_Handler> handlers;

    @Inject
    private MsgPushedHandler_Evt_SCAN() {
        handlers = Util.collectSubInstance(WX_QR_SCENE_ID_Handler.class);
    }

    @Override
    Mono<Void> handle(MsgPushed msgPushed) {
        final String scene_id = Optional.ofNullable(msgPushed.getEventKey()).map(ek -> {
            String prefix = "qrscene_";
            return ek.startsWith(prefix) ? ek.substring(prefix.length()) : ek;
        }).orElse(null);
        if (J.isBlank(scene_id)) {
            return Mono.empty();
        }
        final WX_QR_SCENE_ID_Handler handler = handlers.parallelStream()
                .filter(it -> it.support(scene_id))
                .findFirst()
                .orElse(null);
        if (handler == null) {
            return Mono.empty();
        }
        return ensureWeixinOperator(msgPushed).flatMap(it -> handler.handle(it, msgPushed));
    }

    @Override
    boolean support(MsgPushed msgPushed) {
        return msgPushed.isEvt_SCAN();
    }
}
