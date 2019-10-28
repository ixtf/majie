package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.vertx.graphql.GraphQLMutation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.OperatorService;
import org.jzb.majie.application.command.OperatorUpdateCommand;
import org.jzb.majie.domain.Operator;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLMutation("updateOperator")
public class UpdateOperator extends DataFetchers<Operator> {
    private final OperatorService operatorService;

    @Inject
    private UpdateOperator(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GraphQLMutation("updateOperator")
    @Override
    public void accept(DataFetchingEnvironment env, Promise<Operator> promise) {
        final Principal principal = principal(env);
        final OperatorUpdateCommand command = command(env, OperatorUpdateCommand.class);
        final String id = env.getArgument("id");
        operatorService.update(principal, id, command).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }


}
