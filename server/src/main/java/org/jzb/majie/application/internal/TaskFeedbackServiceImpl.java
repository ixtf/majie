package org.jzb.majie.application.internal;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.TaskFeedbackService;
import org.jzb.majie.application.command.TaskFeedbackUpdateCommand;
import org.jzb.majie.domain.Attachment;
import org.jzb.majie.domain.Task;
import org.jzb.majie.domain.TaskFeedback;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class TaskFeedbackServiceImpl implements TaskFeedbackService {
    private final Jmongo jmongo;

    @Inject
    private TaskFeedbackServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<TaskFeedback> create(Principal principal, TaskFeedbackUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, Mono.just(new TaskFeedback()), command).flatMap(task -> {
            uow.registerNew(task);
            return uow.rxCommit().thenReturn(task);
        });
    }

    private Mono<TaskFeedback> save(Principal principal, Mono<TaskFeedback> taskFeedback$, TaskFeedbackUpdateCommand command) {
        return taskFeedback$.flatMap(taskFeedback -> {
            taskFeedback.setContent(command.getContent());
            return jmongo.find(Task.class, command.getTask().getId()).flatMap(task -> {
                taskFeedback.setTask(task);
                return Flux.fromIterable(J.emptyIfNull(command.getAttachments()))
                        .flatMap(it -> jmongo.find(Attachment.class, it))
                        .collectList();
            }).flatMap(attachments -> {
                taskFeedback.setAttachments(attachments);
                return QueryService.find(jmongo, principal);
            }).map(operator -> {
                taskFeedback.log(operator);
                return taskFeedback;
            });
        });
    }

    @Override
    public Mono<TaskFeedback> update(Principal principal, String id, TaskFeedbackUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, jmongo.find(TaskFeedback.class, id), command).flatMap(taskFeedback -> {
            uow.registerDirty(taskFeedback);
            return uow.rxCommit().thenReturn(taskFeedback);
        });
    }
}
