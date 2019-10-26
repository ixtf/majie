package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.MansionUpdateCommand;
import org.jzb.majie.application.internal.MansionServiceImpl;
import org.jzb.majie.domain.Mansion;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(MansionServiceImpl.class)
public interface MansionService {

    Mono<Mansion> create(Principal principal, MansionUpdateCommand command);

    Mono<Mansion> update(Principal principal, String id, MansionUpdateCommand command);
}
