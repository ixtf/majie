package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.japp.core.exception.JAuthenticationError;
import com.github.ixtf.vertx.Jvertx;
import com.github.ixtf.vertx.graphql.Jgraphql;
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
import org.jzb.majie.MajieModule;

import java.security.Principal;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * @author jzb 2019-09-24
 */
public abstract class DataFetchers<T> implements BiConsumer<DataFetchingEnvironment, Promise<T>> {

    public static RuntimeWiring buildWiring() {
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
                .type("Query", builder -> builder
                        .dataFetcher("listOperator", dataFetcher(ListOperator.class))
                )
                .type("Mutation", builder -> builder
                        .dataFetcher("createOperator", dataFetcher(CreateOperator.class))
                        .dataFetcher("updateOperator", dataFetcher(UpdateOperator.class))

                        .dataFetcher("createMansion", dataFetcher(CreateMansion.class))
                        .dataFetcher("updateMansion", dataFetcher(UpdateMansion.class))

                        .dataFetcher("createTaskGroup", dataFetcher(CreateTaskGroup.class))
                        .dataFetcher("updateTaskGroup", dataFetcher(UpdateTaskGroup.class))

                        .dataFetcher("createTask", dataFetcher(CreateTask.class))
                        .dataFetcher("updateTask", dataFetcher(UpdateTask.class))
                ).build();
    }

    private static VertxDataFetcher dataFetcher(Class<? extends BiConsumer> clazz) {
        final BiConsumer<DataFetchingEnvironment, Promise> instance = MajieModule.getInstance(clazz);
        return new VertxDataFetcher(instance);
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
