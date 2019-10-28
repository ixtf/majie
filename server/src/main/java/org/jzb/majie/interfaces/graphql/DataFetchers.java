package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.japp.core.exception.JAuthenticationError;
import com.github.ixtf.vertx.Jvertx;
import com.github.ixtf.vertx.graphql.GraphQLMutation;
import com.github.ixtf.vertx.graphql.GraphQLQuery;
import com.github.ixtf.vertx.graphql.Jgraphql;
import com.google.common.collect.ImmutableMap;
import com.sun.security.auth.UserPrincipal;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.WiringFactory;
import io.vertx.core.Promise;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.graphql.VertxDataFetcher;
import io.vertx.ext.web.handler.graphql.VertxPropertyDataFetcher;
import org.jzb.majie.Util;

import java.security.Principal;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * @author jzb 2019-09-24
 */
public abstract class DataFetchers<T> implements BiConsumer<DataFetchingEnvironment, Promise<T>> {

    public static RuntimeWiring buildWiring() {
        final ImmutableMap.Builder<String, DataFetcher> queryBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<String, DataFetcher> mutationBuilder = ImmutableMap.builder();
        Util.collectSubInstance(DataFetchers.class).forEach(o -> {
            final VertxDataFetcher dataFetcher = new VertxDataFetcher(o);

            Optional.ofNullable(o.getClass().getAnnotation(GraphQLQuery.class))
                    .map(GraphQLQuery::value)
                    .ifPresent(it -> queryBuilder.put(it, dataFetcher));

            Optional.ofNullable(o.getClass().getAnnotation(GraphQLMutation.class))
                    .map(GraphQLMutation::value)
                    .ifPresent(it -> mutationBuilder.put(it, dataFetcher));
        });
        return RuntimeWiring.newRuntimeWiring()
                .scalar(Jgraphql.getGraphQLLocalDate())
                .scalar(Jgraphql.getGraphQLLocalDateTime())
                .scalar(Jgraphql.getGraphQLMap())
                .wiringFactory(new WiringFactory() {
                    @Override
                    public DataFetcher getDefaultDataFetcher(FieldWiringEnvironment environment) {
                        return new VertxPropertyDataFetcher(environment.getFieldDefinition().getName());
                    }
                })
                .type("Query", builder -> builder.dataFetchers(queryBuilder.build()))
                .type("Mutation", builder -> builder.dataFetchers(mutationBuilder.build()))
                .build();
    }

    public static Principal principal(DataFetchingEnvironment env) {
        final RoutingContext rc = env.getContext();
        return Optional.ofNullable(rc.user())
                .map(User::principal)
                .map(it -> it.getString("uid"))
                .map(UserPrincipal::new)
                .orElseThrow(() -> new JAuthenticationError());
    }

    public static <T> T command(DataFetchingEnvironment env, Class<T> clazz) {
        final T command = MAPPER.convertValue(env.getArgument("command"), clazz);
        return Jvertx.checkAndGetCommand(command);
    }

}
