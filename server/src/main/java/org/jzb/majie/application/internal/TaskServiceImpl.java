package org.jzb.majie.application.internal;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.AuthService;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.TaskService;
import org.jzb.majie.application.command.TaskUpdateCommand;
import org.jzb.majie.domain.Attachment;
import org.jzb.majie.domain.Operator;
import org.jzb.majie.domain.Task;
import org.jzb.majie.domain.TaskGroup;
import org.jzb.majie.domain.data.TaskStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class TaskServiceImpl implements TaskService {
    private final Jmongo jmongo;
    private final AuthService authService;

    @Inject
    private TaskServiceImpl(Jmongo jmongo, AuthService authService) {
        this.jmongo = jmongo;
        this.authService = authService;
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
                return Flux.fromIterable(J.emptyIfNull(command.getAttachments()))
                        .flatMap(it -> jmongo.find(Attachment.class, it))
                        .collectList();
            }).flatMap(attachments -> {
                task.setAttachments(attachments);
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

    @Override
    public Mono<Task> finish(Principal principal, String id) {
        return authService.checkCharger(principal, id).flatMap(tuple2 -> {
            final MongoUnitOfWork uow = jmongo.uow();
            final Operator operator = tuple2.getT1();
            final Task task = tuple2.getT2();
            uow.registerDirty(task);
            task.setStatus(TaskStatus.FINISH);
            task.log(operator);
            return uow.rxCommit().thenReturn(task);
        });
    }

    @Override
    public Mono<Task> run(Principal principal, String id) {
        return authService.checkCharger(principal, id).flatMap(tuple2 -> {
            final MongoUnitOfWork uow = jmongo.uow();
            final Operator operator = tuple2.getT1();
            final Task task = tuple2.getT2();
            uow.registerDirty(task);
            task.setStatus(TaskStatus.RUN);
            task.log(operator);
            return uow.rxCommit().thenReturn(task);
        });
    }
}
