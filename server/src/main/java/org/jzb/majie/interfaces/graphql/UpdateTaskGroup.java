package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.vertx.graphql.GraphQLMutation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.TaskGroupService;
import org.jzb.majie.application.command.TaskGroupUpdateCommand;
import org.jzb.majie.domain.TaskGroup;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLMutation("updateTaskGroup")
public class UpdateTaskGroup extends DataFetchers<TaskGroup> {
    private final TaskGroupService taskGroupService;

    @Inject
    private UpdateTaskGroup(TaskGroupService taskGroupService) {
        this.taskGroupService = taskGroupService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<TaskGroup> promise) {
        final Principal principal = principal(env);
        final TaskGroupUpdateCommand command = command(env, TaskGroupUpdateCommand.class);
        final String id = env.getArgument("id");
        taskGroupService.update(principal, id, command).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }


}
