package org.jzb.majie.interfaces.graphql;

import com.github.ixtf.vertx.graphql.GraphQLMutation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import org.jzb.majie.application.TaskFeedbackService;
import org.jzb.majie.application.command.TaskFeedbackUpdateCommand;
import org.jzb.majie.domain.TaskFeedback;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Singleton
@GraphQLMutation("updateTaskFeedback")
public class UpdateTaskFeedback extends DataFetchers<TaskFeedback> {
    private final TaskFeedbackService taskFeedbackService;

    @Inject
    private UpdateTaskFeedback(TaskFeedbackService taskFeedbackService) {
        this.taskFeedbackService = taskFeedbackService;
    }

    @Override
    public void accept(DataFetchingEnvironment env, Promise<TaskFeedback> promise) {
        final Principal principal = principal(env);
        final TaskFeedbackUpdateCommand command = command(env, TaskFeedbackUpdateCommand.class);
        final String id = env.getArgument("id");
        taskFeedbackService.update(principal, id, command).subscribe(promise::complete, promise::fail, promise::tryComplete);
    }
}
