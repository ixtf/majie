package org.jzb.majie.interfaces.lucene;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.IEntity;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieModule;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
public class LuceneVerticle extends AbstractVerticle {
    public static final String INDEX_ADDRESS = "majie:lucene:index";
    public static final String REMOVE_ADDRESS = "majie:lucene:remove";

    public static JsonObject message(IEntity entity) {
        return new JsonObject()
                .put("class", J.actualClass(entity.getClass()).getName())
                .put("id", entity.getId());
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        CompositeFuture.all(
                Future.<Void>future(p -> indexConsumer().completionHandler(p)),
                Future.<Void>future(p -> removeConsumer().completionHandler(p))
        ).<Void>mapEmpty().setHandler(startFuture);
    }

    private MessageConsumer<JsonObject> indexConsumer() {
        return vertx.eventBus().consumer(INDEX_ADDRESS, reply -> {
            final LuceneService luceneService = MajieModule.getInstance(LuceneService.class);
            final JsonObject body = reply.body();
            final String clazz = body.getString("class");
            final String id = body.getString("id");
            luceneService.index(clazz, id)
                    .doOnError(err -> log.error(clazz + "[" + id + "]", err))
                    .subscribe();
        });
    }

    private MessageConsumer<JsonObject> removeConsumer() {
        return vertx.eventBus().consumer(REMOVE_ADDRESS, reply -> {
            final LuceneService luceneService = MajieModule.getInstance(LuceneService.class);
            final JsonObject body = reply.body();
            final String clazz = body.getString("class");
            final String id = body.getString("id");
            luceneService.remove(clazz, id)
                    .doOnError(err -> log.error(clazz + "[" + id + "]", err))
                    .subscribe();
        });
    }

}
