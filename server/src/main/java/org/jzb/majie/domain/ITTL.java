package org.jzb.majie.domain;

import com.github.ixtf.japp.core.J;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;

/**
 * 过期删除时间
 * 通过 mongo ttl 索引实现，自动删除
 * 时间尽量指定好，并且放在凌晨
 * createIndex( { "expireDateTime": 1 }, { expireAfterSeconds: 0 } )
 *
 * @author jzb 2019-10-26
 */
public interface ITTL {

    static Date calcDate(Date date, Duration duration) {
        final LocalDateTime ldt = J.localDateTime(date).plusNanos(duration.toNanos());
        final int hour = ldt.getHour();
        if (hour > 7) {
            return J.date(ldt.plusDays(1).toLocalDate().atTime(newLt()));
        }
        if (hour < 1) {
            return J.date(ldt.toLocalDate().atTime(newLt()));
        }
        return J.date(ldt);
    }

    static LocalTime newLt() {
        final Random random = new SecureRandom();
        final int hour = random.nextInt(5) + 1;
        final int minute = random.nextInt(59);
        final int second = random.nextInt(59);
        return LocalTime.of(hour, minute, second);
    }

    Date getExpireDateTime();

    void setExpireDateTime(Date expireDateTime);

    /**
     * 过期时间换算，安排在凌晨 (1,7)
     *
     * @param date     创建时间
     * @param duration 过期时间
     */
    default void calcAndSet(Date date, Duration duration) {
        setExpireDateTime(calcDate(date, duration));
    }

}
