package org.jzb.majie.application.internal;

import com.github.ixtf.japp.codec.Jcodec;
import com.github.ixtf.japp.core.exception.JAuthenticationError;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.Util;
import org.jzb.majie.application.AuthService;
import org.jzb.majie.application.command.TokenCommand;
import org.jzb.majie.domain.Attachment;
import org.jzb.majie.domain.Login;
import org.jzb.majie.domain.Operator;
import reactor.core.publisher.Mono;

import java.security.Principal;

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

    @Override
    public Future<DownloadFile> downloadFile(String token) {
        return Future.<User>future(p -> jwtAuth.authenticate(new JsonObject().put("token", token), p)).map(user -> {
            final JsonObject claims = user.principal();
            return Json.decodeValue(claims.encode(), DownloadFile.class);
        });
    }

    @Override
    public String downloadToken(Principal principal, Attachment attachment) {
        final JWTOptions options = new JWTOptions()
                .setAlgorithm(pubSecKeyOptions.getAlgorithm())
                .setIssuer("majie");
        final DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFile(Util.file(attachment));
        downloadFile.setFileName(attachment.getFileName());
        final JsonObject claims = JsonObject.mapFrom(downloadFile);
        return jwtAuth.generateToken(claims, options);
    }
}
