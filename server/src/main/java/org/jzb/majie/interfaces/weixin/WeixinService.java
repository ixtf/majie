package org.jzb.majie.interfaces.weixin;

import com.google.inject.ImplementedBy;
import org.jzb.majie.interfaces.weixin.internal.WeixinServiceImpl;
import org.jzb.weixin.mp.MsgPushed;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(WeixinServiceImpl.class)
public interface WeixinService {

    Mono<Void> handleMsgPushed(MsgPushed msgPushed);

}
