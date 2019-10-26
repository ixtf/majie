package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.reactivestreams.client.MongoCollection;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.bson.Document;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author jzb 2019-10-24
 */
@Singleton
public class ListOperator extends DataFetchers<Map> {
    private final Jmongo jmongo;

    @Inject
    private ListOperator(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Map> promise) {
        final int first = env.getArgument("first");
        final int pageSize = env.getArgument("pageSize");
        final MongoCollection<Document> t_operator = jmongo.collection(Operator.class);
        final Mono<Long> count$ = Mono.from(t_operator.countDocuments());
        final Mono<List<Operator>> operators$ = Flux.from(t_operator.find().skip(first).limit(pageSize).batchSize(pageSize))
                .map(it -> jmongo.toEntity(Operator.class, it))
                .collectList();
        Mono.zip(count$, operators$).map(tuple -> {
            final Long count = tuple.getT1();
            final List<Operator> operators = tuple.getT2();
            return Map.of("count", count, "first", first, "pageSize", pageSize, "operators", operators);
        }).subscribe(promise::complete, promise::fail);
    }
}
