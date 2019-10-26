package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.command.TokenCommand;
import org.jzb.majie.application.internal.AuthServiceImpl;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(AuthServiceImpl.class)
public interface AuthService {

    Mono<String> token(TokenCommand command);

}
