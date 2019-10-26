package org.jzb.majie.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieModule;
import org.jzb.majie.application.WeixinService;

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
            weixinService.msgPushed(reply.body());
        }).completionHandler(startFuture);
    }

}
