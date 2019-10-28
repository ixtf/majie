package org.jzb.majie.verticle;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.vertx.route.RoutingContextEnvelope;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieModule;
import org.jzb.majie.application.AttachmentService;
import org.jzb.majie.domain.Attachment;
import reactor.core.publisher.Flux;

import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
public class FileUploadsVerticle extends AbstractVerticle {
    public static final String ADDRESS = "majie:fileUploads";

    public static JsonObject message(RoutingContext rc) {
        final JsonObject principal = rc.user().principal();
        final JsonArray fileUploads = new JsonArray();
        J.emptyIfNull(rc.fileUploads()).stream()
                .map(FileUploadsVerticle::toJsonObject)
                .forEach(fileUploads::add);
        return new JsonObject().put("principal", principal).put("fileUploads", fileUploads);
    }

    private static JsonObject toJsonObject(FileUpload fileUpload) {
        return new JsonObject().put("uploadedFileName", fileUpload.uploadedFileName())
                .put("fileName", fileUpload.fileName())
                .put("contentType", fileUpload.contentType())
                .put("size", fileUpload.size())
                .put("name", fileUpload.name())
                .put("charSet", fileUpload.charSet());
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        vertx.eventBus().<JsonObject>consumer(ADDRESS, reply -> {
            final AttachmentService attachmentService = MajieModule.getInstance(AttachmentService.class);
            final JsonObject body = reply.body();
            final Principal principal = RoutingContextEnvelope.defaultPrincipalFun(body);
            Flux.fromIterable(body.getJsonArray("fileUploads")).map(JsonObject.class::cast).map(jsonObject -> {
                final Attachment attachment = new Attachment();
                attachment.setFileName(jsonObject.getString("fileName"));
                attachment.setContentType(jsonObject.getString("contentType"));
                attachment.setSize(jsonObject.getLong("size"));
                return attachmentService.handleUpload(principal, jsonObject.getString("uploadedFileName"), attachment);
            }).map(JsonObject::mapFrom).collectList().map(it -> {
                final JsonArray jsonArray = new JsonArray();
                it.stream().forEach(jsonArray::add);
                return jsonArray;
            }).subscribe(reply::reply, err -> reply.fail(400, err.getMessage()));
        }).completionHandler(startFuture);
    }

}
