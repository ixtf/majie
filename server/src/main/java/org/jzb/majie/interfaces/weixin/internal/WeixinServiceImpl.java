package org.jzb.majie.interfaces.weixin.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.Util;
import org.jzb.majie.interfaces.weixin.WeixinService;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class WeixinServiceImpl implements WeixinService {
    private final Collection<MsgPushedHandler> handlers;

    @Inject
    private WeixinServiceImpl() {
        handlers = Util.collectSubInstance(MsgPushedHandler.class);
    }

    @Override
    public Mono<Void> handleMsgPushed(MsgPushed msgPushed) {
        return Flux.fromIterable(handlers)
                .filter(it -> it.support(msgPushed))
                .next()
                .flatMap(it -> it.handle(msgPushed))
                .then();
    }

}
