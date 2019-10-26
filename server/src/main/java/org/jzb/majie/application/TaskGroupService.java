package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.TaskGroupUpdateCommand;
import org.jzb.majie.application.internal.TaskGroupServiceImpl;
import org.jzb.majie.domain.TaskGroup;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(TaskGroupServiceImpl.class)
public interface TaskGroupService {
    Mono<TaskGroup> create(Principal principal, TaskGroupUpdateCommand command);

    Mono<TaskGroup> update(Principal principal, String id, TaskGroupUpdateCommand command);
}
