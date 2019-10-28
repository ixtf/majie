package org.jzb.majie.interfaces.weixin.internal.WX_QR_SCENE_ID;

import com.github.ixtf.persistence.mongo.Jmongo;
import org.jzb.majie.MajieModule;
import org.jzb.majie.domain.WeixinOperator;
import org.jzb.weixin.mp.MpClient;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2019-10-27
 */
public abstract class WX_QR_SCENE_ID_Handler {
    protected final MpClient mpClient;
    protected final Jmongo jmongo;

    protected WX_QR_SCENE_ID_Handler() {
        mpClient = MajieModule.getInstance(MpClient.class);
        jmongo = MajieModule.getInstance(Jmongo.class);
    }

    public abstract Mono handle(WeixinOperator weixinOperator, MsgPushed msgPushed);

    public abstract boolean support(String scene_id);

}
