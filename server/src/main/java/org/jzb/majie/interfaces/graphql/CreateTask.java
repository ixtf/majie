package org.jzb.majie.interfaces.graphql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.TaskService;
import org.jzb.majie.application.command.TaskUpdateCommand;
import org.jzb.majie.domain.Task;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Singleton
public class CreateTask extends DataFetchers<Task> {
    private final TaskService taskService;

    @Inject
    private CreateTask(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<Task> promise) {
        final Principal principal = principal(env);
        final TaskUpdateCommand command = command(env, TaskUpdateCommand.class);
        taskService.create(principal, command).subscribe(promise::complete, promise::fail);
    }
}
