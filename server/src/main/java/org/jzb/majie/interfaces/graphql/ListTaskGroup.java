package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.vertx.graphql.GraphQLQuery;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.reactivestreams.client.MongoCollection;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.bson.Document;
import org.jzb.majie.domain.TaskGroup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLQuery("listTaskGroup")
public class ListTaskGroup extends DataFetchers<Map> {
    private final Jmongo jmongo;

    @Inject
    private ListTaskGroup(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Map> promise) {
        final int first = env.getArgument("first");
        final int pageSize = env.getArgument("pageSize");
        final MongoCollection<Document> collection = jmongo.collection(TaskGroup.class);
        final Mono<Long> count$ = Mono.from(collection.countDocuments());
        final Mono<List<TaskGroup>> result$ = Flux.from(collection.find().skip(first).limit(pageSize).batchSize(pageSize))
                .map(it -> jmongo.toEntity(TaskGroup.class, it))
                .collectList();
        Mono.zip(count$, result$).map(tuple -> {
            final Long count = tuple.getT1();
            final List<TaskGroup> result = tuple.getT2();
            return Map.of("count", count, "first", first, "pageSize", pageSize, "result", result);
        }).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }
}
