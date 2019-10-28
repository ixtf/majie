package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.TaskFeedbackUpdateCommand;
import org.jzb.majie.application.internal.TaskFeedbackServiceImpl;
import org.jzb.majie.domain.TaskFeedback;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(TaskFeedbackServiceImpl.class)
public interface TaskFeedbackService {
    Mono<TaskFeedback> create(Principal principal, TaskFeedbackUpdateCommand command);

    Mono<TaskFeedback> update(Principal principal, String id, TaskFeedbackUpdateCommand command);
}
