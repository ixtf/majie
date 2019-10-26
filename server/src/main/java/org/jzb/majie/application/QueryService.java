package org.jzb.majie.application;

import com.github.ixtf.persistence.mongo.Jmongo;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
public interface QueryService {

    static Mono<Operator> find(Jmongo jmongo, Principal principal) {
        return jmongo.find(Operator.class, principal.getName());
    }

}
