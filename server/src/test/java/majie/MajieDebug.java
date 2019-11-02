package majie;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieLauncher;
import org.jzb.majie.MajieModule;
import org.jzb.weixin.mp.MpClient;
import org.jzb.weixin.mp.qrcode.MpQrcodeResponse;

import static org.jzb.majie.domain.data.WX_QR_SCENE_ID.TASK_CHARGE_INVITE;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
public class MajieDebug {

    public static void main(String[] args) {
        MajieLauncher.main(new String[]{});
    }

    private static void token() {
        final JWTAuth jwtAuth = MajieModule.getInstance(JWTAuth.class);
        final PubSecKeyOptions pubSecKeyOptions = MajieModule.getInstance(PubSecKeyOptions.class);
        final JWTOptions options = new JWTOptions()
                .setAlgorithm(pubSecKeyOptions.getAlgorithm())
                .setSubject("jzb")
                .setIssuer("majie");
        final JsonObject claims = new JsonObject().put("uid", "jzb");
        final String token = jwtAuth.generateToken(claims, options);
        System.out.println(token);

        jwtAuth.authenticate(new JsonObject().put("jwt", token), ar -> {
            if (ar.succeeded()) {
                System.out.println(ar.result().principal());
            }
        });
    }

    @SneakyThrows
    private static void qrcode() {
        final MpClient mpClient = MajieModule.getInstance(MpClient.class);
        final MpQrcodeResponse res = mpClient.qrcode()
                .expire_seconds(2592000)
                .action_name("QR_SCENE")
                .scene_id(TASK_CHARGE_INVITE.scene_id())
                .call();
        System.out.println(res);
    }

    private static void verifyUrl() {
        final MpClient mpClient = MajieModule.getInstance(MpClient.class);
        final String msgSignature = "a229a77b7eac4e733396fd4b8f91386891b18ab4";
        final String timeStamp = "1571903119";
        final String nonce = "865189352";
        final String echoStr = "3828079931883519743";
        final String s = mpClient.verifyUrl(msgSignature, timeStamp, nonce, echoStr);
        System.out.println(s);
    }

    private static void decryptMsg() {
        final MpClient mpClient = MajieModule.getInstance(MpClient.class);
        final String msgSignature = "774e72b892f4054ad20f9c33101053cae359ddb1";
        final String timeStamp = "1571903500";
        final String nonce = "357373245";
        final String postData = "<xml>\n" +
                "    <ToUserName><![CDATA[gh_04c41b598f07]]></ToUserName>\n" +
                "    <Encrypt><![CDATA[zS9G9Tcvhg3HtHfrd3cOw1MtWe22KrocYs/kb2J3nVhzb0HP7MZVsCAE6TU90bDKckIbS03y1hai8F1sqGYNIGoouuSLUOQEyTHxVzB9PyfTgVw/W7xVnscGri8V/LT1RL55EpYJpnXY5L0np91l75+9t+VrUliSh88uUPMvjcigNZD/ozZzUR6dKDKglBcDrGqNH4Hw8aMupd3HFRit07HhuVGly6K6nF2wUR6FIgjYbGWBp48TU7HnYwSElw8MJQ306LipyksvWgKsSyYzOPvrrxLrK6DEPjAxC11+0kbKqhgHs2w/zcpoP7U8hzhrCqM4nRqERTIdsxILW3o1ntp4WnxTC9eWlFmZuTgenppqy+ajgROH1zE2VGXJQtlLcVqHPt4q1OxQY+zPII/5409j+Jc38S2wgb8jRX8Vynk=]]></Encrypt>\n" +
                "</xml>";
        final String s = mpClient.decryptMsg(msgSignature, timeStamp, nonce, postData);
        System.out.println(s);
    }
}
