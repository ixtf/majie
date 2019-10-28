package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.vertx.graphql.GraphQLQuery;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.query.TaskQuery;
import org.jzb.majie.domain.Task;
import org.jzb.majie.interfaces.lucene.LuceneService;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLQuery("listTask")
public class ListTask extends DataFetchers<Map> {
    private final Jmongo jmongo;
    private final LuceneService luceneService;

    @Inject
    private ListTask(Jmongo jmongo, LuceneService luceneService) {
        this.jmongo = jmongo;
        this.luceneService = luceneService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Map> promise) {
        final int first = env.getArgument("first");
        final int pageSize = env.getArgument("pageSize");
        TaskQuery.builder()
                .principal(principal(env))
                .first(first)
                .pageSize(pageSize)
                .mansionId(env.getArgument("mansionId"))
                .taskGroupId(env.getArgument("taskGroupId"))
                .build()
                .rxQuery(luceneService)
                .flatMap(pair -> {
                    final long count = pair.getKey();
                    return Flux.fromIterable(pair.getRight())
                            .flatMap(it -> jmongo.find(Task.class, it))
                            .collectList()
                            .map(result -> Map.of("count", count, "first", first, "pageSize", pageSize, "result", result));
                }).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }

}
