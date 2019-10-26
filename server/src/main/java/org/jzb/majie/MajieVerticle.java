package org.jzb.majie;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import org.jzb.majie.verticle.AgentVerticle;
import org.jzb.majie.verticle.LuceneVerticle;
import org.jzb.majie.verticle.WeixinInterfaceVerticle;
import org.jzb.majie.verticle.WorkerVerticle;

/**
 * @author jzb 2019-10-24
 */
public class MajieVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        MajieModule.init(vertx);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        CompositeFuture.all(
                deployWeixinInterfaceVerticle(),
                deployLuceneVerticle(),
                deployWorkVerticle()
        ).compose(it -> deployAgentVerticle()).<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployWeixinInterfaceVerticle() {
        return Future.future(p -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000);
            vertx.deployVerticle(WeixinInterfaceVerticle.class, deploymentOptions, p);
        });
    }

    private Future<String> deployLuceneVerticle() {
        return Future.future(p -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000);
            vertx.deployVerticle(LuceneVerticle.class, deploymentOptions, p);
        });
    }

    private Future<String> deployWorkVerticle() {
        return Future.future(p -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000);
            vertx.deployVerticle(WorkerVerticle.class, deploymentOptions, p);
        });
    }

    private Future<String> deployAgentVerticle() {
        return Future.future(p -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setInstances(20);
            vertx.deployVerticle(AgentVerticle.class, deploymentOptions, p);
        });
    }
}
