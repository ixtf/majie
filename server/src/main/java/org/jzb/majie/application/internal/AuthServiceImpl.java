package org.jzb.majie.application.internal;

import com.github.ixtf.japp.codec.Jcodec;
import com.github.ixtf.japp.core.exception.JAuthenticationError;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.application.AuthService;
import org.jzb.majie.application.command.TokenCommand;
import org.jzb.majie.domain.Login;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class AuthServiceImpl implements AuthService {
    private final JWTAuth jwtAuth;
    private final PubSecKeyOptions pubSecKeyOptions;
    private final Jmongo jmongo;

    @Inject
    private AuthServiceImpl(JWTAuth jwtAuth, PubSecKeyOptions pubSecKeyOptions, Jmongo jmongo) {
        this.jwtAuth = jwtAuth;
        this.pubSecKeyOptions = pubSecKeyOptions;
        this.jmongo = jmongo;
    }

    @Override
    public Mono<String> token(TokenCommand command) {
        return jmongo.find(Login.class, eq("loginId", command.getLoginId())).flatMap(login -> {
            if (Jcodec.checkPassword(login.getPassword(), command.getLoginPassword())) {
                return jmongo.find(Operator.class, login.getId());
            }
            throw new JAuthenticationError();
        }).map(operator -> {
            if (operator.isDeleted()) {
                throw new JAuthenticationError();
            }
            final String uid = operator.getId();
            final JWTOptions options = new JWTOptions()
                    .setAlgorithm(pubSecKeyOptions.getAlgorithm())
                    .setSubject(uid)
                    .setIssuer("majie");
            final JsonObject claims = new JsonObject().put("uid", uid);
            return jwtAuth.generateToken(claims, options);
        });
    }
}
