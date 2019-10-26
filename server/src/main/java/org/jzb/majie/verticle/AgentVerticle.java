package org.jzb.majie.verticle;

import com.github.ixtf.vertx.CorsConfig;
import com.github.ixtf.vertx.Jvertx;
import com.github.ixtf.vertx.ws.rs.JaxRsRouteResolver;
import com.google.common.collect.Sets;
import graphql.GraphQL;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.graphql.ApolloWSHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import org.apache.commons.io.FileUtils;
import org.jzb.majie.MajieModule;
import org.jzb.majie.application.AuthService;
import org.jzb.majie.application.AuthService.DownloadFile;

import java.net.URLEncoder;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.jzb.majie.verticle.FileUploadsVerticle.message;

/**
 * @author jzb 2019-10-24
 */
public class AgentVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        final Router router = Jvertx.router(vertx, new CorsConfig());
        router.route().handler(BodyHandler.create().setUploadsDirectory(FileUtils.getTempDirectoryPath()));
        router.route().handler(ResponseContentTypeHandler.create());
        router.route("/status").handler(HealthCheckHandler.create(vertx));

        router.post("/downloads/:token").produces(APPLICATION_OCTET_STREAM).handler(rc -> {
            final AuthService authService = MajieModule.getInstance(AuthService.class);
            authService.downloadFile(rc.pathParam("token")).setHandler(ar -> {
                if (ar.succeeded()) {
                    final DownloadFile downloadFile = ar.result();
                    final String fileName = downloadFile.getFileName();
                    final String encodeFileName = URLEncoder.encode(fileName, UTF_8);
                    rc.response().putHeader("Content-Disposition", "attachment;filename=" + encodeFileName)
                            .sendFile(downloadFile.getFilePath());
                } else {
                    rc.fail(ar.cause());
                }
            });
        });

        final JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(MajieModule.getInstance(JWTAuth.class));
        router.route("/graphql/*").handler(jwtAuthHandler);
        router.route("/api/*").handler(jwtAuthHandler);

        final GraphQL graphQL = MajieModule.getInstance(GraphQL.class);
        router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
        router.route("/graphql").handler(GraphQLHandler.create(graphQL));
        final GraphiQLHandlerOptions options = new GraphiQLHandlerOptions().setEnabled(true);
        router.route("/graphiql/*").handler(GraphiQLHandler.create(options).graphiQLRequestHeaders(rc -> {
            final String token = rc.get("token");
            return MultiMap.caseInsensitiveMultiMap().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }));

        router.post("/api/attachments").handler(rc -> vertx.eventBus().<JsonArray>request(FileUploadsVerticle.ADDRESS, message(rc), ar -> {
            if (ar.succeeded()) {
                rc.response().end(ar.result().body().encode());
            } else {
                rc.fail(ar.cause());
            }
        }));

        Jvertx.resolve(AgentResolver.class).forEach(it -> it.router(router, MajieModule::getInstance));

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true)
                .setWebsocketSubProtocols("graphql-ws");
        Future.<HttpServer>future(promise -> vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(8080, promise))
                .<Void>mapEmpty()
                .setHandler(startFuture);
    }

    public static class AgentResolver extends JaxRsRouteResolver {

        @Override
        protected Set<String> getPackages() {
            return Sets.newHashSet("org.jzb.majie.interfaces.rest");
        }

        @Override
        protected Set<Class> getClasses() {
            return null;
        }
    }

}
