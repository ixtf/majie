package org.jzb.majie.application.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.TaskGroupService;
import org.jzb.majie.application.command.TaskGroupUpdateCommand;
import org.jzb.majie.domain.Mansion;
import org.jzb.majie.domain.TaskGroup;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class TaskGroupServiceImpl implements TaskGroupService {
    private final Jmongo jmongo;

    @Inject
    private TaskGroupServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<TaskGroup> create(Principal principal, TaskGroupUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, Mono.just(new TaskGroup()), command).flatMap(taskGroup -> {
            uow.registerNew(taskGroup);
            return uow.rxCommit().thenReturn(taskGroup);
        });
    }

    private Mono<TaskGroup> save(Principal principal, Mono<TaskGroup> taskGroup$, TaskGroupUpdateCommand command) {
        return taskGroup$.flatMap(taskGroup -> {
            taskGroup.setName(command.getName());
            return jmongo.find(Mansion.class, command.getMansion().getId()).flatMap(mansion -> {
                taskGroup.setMansion(mansion);
                return QueryService.find(jmongo, principal);
            }).map(operator -> {
                taskGroup.log(operator);
                return taskGroup;
            });
        });
    }

    @Override
    public Mono<TaskGroup> update(Principal principal, String id, TaskGroupUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, jmongo.find(TaskGroup.class, id), command).flatMap(taskGroup -> {
            uow.registerDirty(taskGroup);
            return uow.rxCommit().thenReturn(taskGroup);
        });
    }
}
