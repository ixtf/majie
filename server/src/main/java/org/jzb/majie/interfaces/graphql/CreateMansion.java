package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.vertx.graphql.GraphQLMutation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.MansionService;
import org.jzb.majie.application.command.MansionUpdateCommand;
import org.jzb.majie.domain.Mansion;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLMutation("createMansion")
public class CreateMansion extends DataFetchers<Mansion> {
    private final MansionService mansionService;

    @Inject
    private CreateMansion(MansionService mansionService) {
        this.mansionService = mansionService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Mansion> promise) {
        final Principal principal = principal(env);
        final MansionUpdateCommand command = command(env, MansionUpdateCommand.class);
        mansionService.create(principal, command).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }

}
