package org.jzb.majie.interfaces.weixin;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieModule;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
public class WeixinInterfaceVerticle extends AbstractVerticle {
    public static final String ADDRESS = "majie:weixin-mp:WeixinInterface";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        vertx.eventBus().<String>consumer(ADDRESS, reply -> {
            final WeixinService weixinService = MajieModule.getInstance(WeixinService.class);
            Mono.fromCallable(() -> new MsgPushed(reply.body()))
                    .subscribeOn(Schedulers.elastic())
                    .flatMap(weixinService::handleMsgPushed)
                    .doOnError(err -> log.error("", err))
                    .subscribe();
        }).completionHandler(startFuture);
    }

}
