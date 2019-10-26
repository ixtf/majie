package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.OperatorUpdateCommand;
import org.jzb.majie.application.internal.OperatorServiceImpl;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(OperatorServiceImpl.class)
public interface OperatorService {
    Mono<Operator> create(Principal principal, OperatorUpdateCommand command);

    Mono<Operator> update(Principal principal, String id, OperatorUpdateCommand command);
}
