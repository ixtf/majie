package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.internal.WeixinServiceImpl;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(WeixinServiceImpl.class)
public interface WeixinService {
    void msgPushed(String body);
}
