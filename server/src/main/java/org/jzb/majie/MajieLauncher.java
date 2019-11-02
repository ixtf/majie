package org.jzb.majie;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

/**
 * @author jzb 2019-11-01
 */
public class MajieLauncher extends Launcher {
    public static void main(String[] args) {
        new MajieLauncher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        final VertxPrometheusOptions prometheusOptions = new VertxPrometheusOptions()
                .setEnabled(true);
        final MicrometerMetricsOptions metricsOptions = new MicrometerMetricsOptions()
                .setPrometheusOptions(prometheusOptions)
                .setEnabled(true);
        options.setMetricsOptions(metricsOptions);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        MajieModule.init(vertx);
    }

    @Override
    protected String getMainVerticle() {
        return MajieVerticle.class.getName();
    }

}
