package org.jzb.majie.interfaces.rest;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import org.jzb.majie.verticle.WeixinInterfaceVerticle;
import org.jzb.weixin.mp.MpClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author jzb 2019-10-24
 */
@Path("WeixinInterface")
@Produces(TEXT_PLAIN)
public class WeixinInterfaceResource {
    private final Vertx vertx;
    private final MpClient mpClient;

    @Inject
    private WeixinInterfaceResource(Vertx vertx, MpClient mpClient) {
        this.vertx = vertx;
        this.mpClient = mpClient;
    }

    @GET
    public Mono<String> get(@QueryParam("signature") String signature,
                            @QueryParam("timestamp") String timestamp,
                            @QueryParam("nonce") String nonce,
                            @QueryParam("echostr") String echostr) {
// fixme 验证token不成功
        System.out.println(echostr);
        return Mono.just(echostr);
//            return mpClient.verifyUrl(signature, timestamp, nonce, echostr);
    }

    @POST
    public Mono<Void> post(@QueryParam("signature") String signature,
                           @QueryParam("timestamp") String timestamp,
                           @QueryParam("nonce") String nonce,
                           @QueryParam("msg_signature") String msg_signature,
                           @QueryParam("encrypt_type") String encrypt_type,
                           String postData) {
        return Mono.fromCallable(() -> "aes".equalsIgnoreCase(encrypt_type) ? mpClient.decryptMsg(msg_signature, timestamp, nonce, postData) : postData)
                .subscribeOn(Schedulers.elastic())
                .doOnNext(it -> vertx.eventBus().send(WeixinInterfaceVerticle.ADDRESS, it))
                .then();
    }
}
