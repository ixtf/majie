package org.jzb.majie.application.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jzb.majie.application.MansionService;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.command.MansionUpdateCommand;
import org.jzb.majie.domain.Mansion;
import org.jzb.majie.domain.Operator;
import org.jzb.majie.domain.TaskGroup;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class MansionServiceImpl implements MansionService {
    private final Jmongo jmongo;

    @Inject
    private MansionServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<Mansion> create(Principal principal, MansionUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, Mono.just(new Mansion()), command).flatMap(mansion -> {
            uow.registerNew(mansion);
            mansion.setId(ObjectId.get().toHexString());

            // 新建大厦，需要默认 计划组
            final TaskGroup taskGroup = new TaskGroup();
            uow.registerNew(taskGroup);
            taskGroup.setId(mansion.getId());
            taskGroup.setMansion(mansion);

            final Operator creator = mansion.getCreator();
            if (creator.getMansion() == null) {
                uow.registerDirty(creator);
                creator.setMansion(mansion);
            }
            return uow.rxCommit().thenReturn(mansion);
        });
    }

    private Mono<Mansion> save(Principal principal, Mono<Mansion> mansion$, MansionUpdateCommand command) {
        return mansion$.flatMap(mansion -> {
            mansion.setName(command.getName());
            return QueryService.find(jmongo, principal).map(operator -> {
                mansion.log(operator);
                return mansion;
            });
        });
    }

    @Override
    public Mono<Mansion> update(Principal principal, String id, MansionUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, jmongo.find(Mansion.class, id), command).flatMap(mansion -> {
            uow.registerDirty(mansion);
            return uow.rxCommit().thenReturn(mansion);
        });
    }
}
