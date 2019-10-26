package org.jzb.majie.interfaces.graphql;

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
public class UpdateOperator extends DataFetchers<Operator> {
    private final OperatorService operatorService;

    @Inject
    private UpdateOperator(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Operator> promise) {
        final Principal principal = principal(env);
        final OperatorUpdateCommand command = command(env, OperatorUpdateCommand.class);
        final String id = env.getArgument("id");
        operatorService.update(principal, id, command).subscribe(promise::complete, promise::fail);
    }
}