package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntity;
import lombok.*;
import org.jzb.weixin.mp.unionInfo.MpUnionInfoResponse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * @author jzb 2019-10-24
 */
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class WeixinOperator implements IEntity {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Id
    @NotBlank
    private String id;
    @Getter
    @Setter
    @Column
    @NotBlank
    private Operator operator;
    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    @Getter
    @Setter
    @Column
    @NotBlank
    private String unionid;
    /**
     * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     */
    @Getter
    @Setter
    @Column
    private int subscribe;
    @Getter
    @Setter
    @Column
    private String nickname;
    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    @Getter
    @Setter
    @Column
    private int sex;
    @Getter
    @Setter
    @Column
    private String city;
    @Getter
    @Setter
    @Column
    private String country;
    @Getter
    @Setter
    @Column
    private String province;
    /**
     * 用户的语言，简体中文为zh_CN
     */
    @Getter
    @Setter
    @Column
    private String language;
    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），
     * <p>
     * 用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    @Getter
    @Setter
    @Column
    private String headimgurl;
    /**
     * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     */
    @Getter
    @Setter
    @Column
    private long subscribe_time;

    @Getter
    @Setter
    @Column
    private boolean deleted;

    public void setUnionInfo(MpUnionInfoResponse res) {
        this.id = res.openid();
        this.unionid = res.unionid();
        this.subscribe = res.subscribe();
        this.headimgurl = res.headimgurl();
        this.nickname = res.nickname();
        this.sex = res.sex();
        this.city = res.city();
        this.country = res.country();
        this.province = res.province();
        this.language = res.language();
        this.subscribe_time = res.subscribe_time();
    }
}
