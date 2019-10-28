package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.TaskUpdateCommand;
import org.jzb.majie.application.internal.TaskServiceImpl;
import org.jzb.majie.domain.Task;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(TaskServiceImpl.class)
public interface TaskService {
    Mono<Task> create(Principal principal, TaskUpdateCommand command);

    Mono<Task> update(Principal principal, String id, TaskUpdateCommand command);

    Mono<Task> finish(Principal principal, String id);

    Mono<Task> run(Principal principal, String id);
}
