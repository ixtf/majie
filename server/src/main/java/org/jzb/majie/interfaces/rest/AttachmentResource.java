package org.jzb.majie.interfaces.rest;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import org.jzb.majie.verticle.WeixinInterfaceVerticle;
import org.jzb.weixin.mp.MpClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author jzb 2019-10-24
 */
@Path("WeixinInterface")
@Produces(TEXT_PLAIN)
public class AttachmentResource {
    private final Vertx vertx;
    private final MpClient mpClient;

    @Inject
    private AttachmentResource(Vertx vertx, MpClient mpClient) {
        this.vertx = vertx;
        this.mpClient = mpClient;
    }

    @POST
    public Mono<Void> post(@QueryParam("encrypt_type") String encrypt_type, @QueryParam("msg_signature") String msg_signature, @QueryParam("signature") String signature, @QueryParam("timestamp") String timestamp, @QueryParam("nonce") String nonce, String postData) {
        return Mono.fromCallable(() -> "aes".equalsIgnoreCase(encrypt_type) ? mpClient.decryptMsg(msg_signature, timestamp, nonce, postData) : postData)
                .subscribeOn(Schedulers.elastic())
                .doOnNext(it -> vertx.eventBus().send(WeixinInterfaceVerticle.ADDRESS, it))
                .then();
    }
}
