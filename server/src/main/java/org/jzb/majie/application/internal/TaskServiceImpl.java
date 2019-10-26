package org.jzb.majie.application.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.TaskService;
import org.jzb.majie.application.command.TaskUpdateCommand;
import org.jzb.majie.domain.Task;
import org.jzb.majie.domain.TaskGroup;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class TaskServiceImpl implements TaskService {
    private final Jmongo jmongo;

    @Inject
    private TaskServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<Task> create(Principal principal, TaskUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, Mono.just(new Task()), command).flatMap(task -> {
            uow.registerNew(task);
            return uow.rxCommit().thenReturn(task);
        });
    }

    private Mono<Task> save(Principal principal, Mono<Task> task$, TaskUpdateCommand command) {
        return task$.flatMap(task -> {
            task.setTitle(command.getTitle());
            task.setContent(command.getContent());
            return jmongo.find(TaskGroup.class, command.getGroup().getId()).flatMap(taskGroup -> {
                task.setGroup(taskGroup);
                return QueryService.find(jmongo, principal);
            }).map(operator -> {
                task.log(operator);
                return task;
            });
        });
    }

    @Override
    public Mono<Task> update(Principal principal, String id, TaskUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, jmongo.find(Task.class, id), command).flatMap(task -> {
            uow.registerDirty(task);
            return uow.rxCommit().thenReturn(task);
        });
    }
}
