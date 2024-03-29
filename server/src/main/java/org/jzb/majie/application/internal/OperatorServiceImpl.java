package org.jzb.majie.application.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.OperatorService;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.application.command.OperatorUpdateCommand;
import org.jzb.majie.domain.Mansion;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class OperatorServiceImpl implements OperatorService {
    private final Jmongo jmongo;

    @Inject
    private OperatorServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<Operator> create(Principal principal, OperatorUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, Mono.just(new Operator()), command).flatMap(operator -> {
            uow.registerNew(operator);
            return uow.rxCommit().thenReturn(operator);
        });
    }

    private Mono<Operator> save(Principal principal, Mono<Operator> operator$, OperatorUpdateCommand command) {
        return operator$.map(operator -> {
            operator.setName(command.getName());
            operator.setAdmin(command.isAdmin());
            return operator;
        });
    }

    @Override
    public Mono<Operator> update(Principal principal, String id, OperatorUpdateCommand command) {
        final MongoUnitOfWork uow = jmongo.uow();
        return save(principal, jmongo.find(Operator.class, id), command).flatMap(operator -> {
            uow.registerDirty(operator);
            return uow.rxCommit().thenReturn(operator);
        });
    }

    @Override
    public Mono<Operator> defaultMansion(Principal principal, String mansionId) {
        final MongoUnitOfWork uow = jmongo.uow();
        final Mono<Operator> operator$ = QueryService.find(jmongo, principal);
        final Mono<Mansion> mansion$ = jmongo.find(Mansion.class, mansionId);
        return Mono.zip(operator$, mansion$).flatMap(tuple2 -> {
            final Operator operator = tuple2.getT1();
            uow.registerDirty(operator);
            final Mansion mansion = tuple2.getT2();
            operator.setMansion(mansion);
            return uow.rxCommit().thenReturn(operator);
        });
    }
}
