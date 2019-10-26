package org.jzb.majie.verticle;

import com.github.ixtf.vertx.Jvertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.jzb.majie.MajieModule;
import org.jzb.majie.verticle.AgentVerticle.AgentResolver;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author jzb 2019-10-24
 */
public class WorkerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        final List<Future> futures = Jvertx.resolve(AgentResolver.class)
                .map(it -> it.consumer(vertx, MajieModule::getInstance))
                .collect(toList());
        CompositeFuture.all(futures).<Void>mapEmpty().setHandler(startFuture);
    }

}
