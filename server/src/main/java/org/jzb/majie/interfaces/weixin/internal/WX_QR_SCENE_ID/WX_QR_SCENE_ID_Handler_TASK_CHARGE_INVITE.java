package org.jzb.majie.interfaces.weixin.internal.WX_QR_SCENE_ID;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jzb.majie.domain.Operator;
import org.jzb.majie.domain.TasksInvite;
import org.jzb.majie.domain.WeixinOperator;
import org.jzb.weixin.mp.MsgPushed;
import org.jzb.weixin.mp.msg_kf.MpMsgKfResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static org.jzb.majie.domain.data.WX_QR_SCENE_ID.TASK_CHARGE_INVITE;

/**
 * @author jzb 2019-10-27
 */
@Slf4j
@Singleton
public class WX_QR_SCENE_ID_Handler_TASK_CHARGE_INVITE extends WX_QR_SCENE_ID_Handler {

    @Inject
    private WX_QR_SCENE_ID_Handler_TASK_CHARGE_INVITE() {
    }

    @Override
    public Mono handle(WeixinOperator weixinOperator, MsgPushed msgPushed) {
        final Operator operator = Optional.ofNullable(weixinOperator)
                .map(WeixinOperator::getOperator)
                .orElse(null);
        if (operator == null) {
            return Mono.empty();
        }
        final Date currentDate = new Date();
        final String ticket = msgPushed.getTicket();
        return jmongo.find(TasksInvite.class, ticket).flatMap(tasksInvite -> {
            final MongoUnitOfWork uow = jmongo.uow();
            tasksInvite.getTasks().stream().forEach(task -> {
                uow.registerDirty(task);
                final Collection<Operator> chargers = Sets.newHashSet(operator);
                chargers.addAll(J.emptyIfNull(task.getChargers()));
                task.setChargers(chargers);
                task.log(operator, currentDate);
            });
            return uow.rxCommit().then(retCallback(msgPushed));
        });
    }

    private Mono retCallback(MsgPushed msgPushed) {
        return Mono.fromCallable(() -> {
            final String content = "加入成功！";
            final MpMsgKfResponse res = mpClient.msgKf(msgPushed.getFromUserName()).text().content(content).call();
            if (!res.isSuccessed()) {
                log.error(res.errmsg());
            }
            return res;
        });
    }

    @Override
    public boolean support(String scene_id) {
        return Optional.ofNullable(scene_id)
                .filter(NumberUtils::isParsable)
                .map(Integer::parseInt)
                .map(it -> TASK_CHARGE_INVITE.scene_id() == it)
                .orElse(false);
    }
}
