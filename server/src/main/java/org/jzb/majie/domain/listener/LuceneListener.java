package org.jzb.majie.domain.listener;

import com.github.ixtf.persistence.IEntity;
import io.vertx.core.Vertx;
import org.jzb.majie.MajieModule;
import org.jzb.majie.interfaces.lucene.LuceneVerticle;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author jzb 2019-10-25
 */
public class LuceneListener {

    @PostPersist
    @PostUpdate
    private void index(IEntity entity) {
        final Vertx vertx = MajieModule.getInstance(Vertx.class);
        vertx.eventBus().send(LuceneVerticle.INDEX_ADDRESS, LuceneVerticle.message(entity));
    }

    @PostRemove
    private void remove(IEntity entity) {
        final Vertx vertx = MajieModule.getInstance(Vertx.class);
        vertx.eventBus().send(LuceneVerticle.REMOVE_ADDRESS, LuceneVerticle.message(entity));
    }

}
