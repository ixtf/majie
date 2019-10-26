package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntity;
import lombok.*;

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
public class Login implements IEntity {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Id
    @NotBlank
    private String id;
    @Getter
    @Setter
    @Column(unique = true)
    @NotBlank
    private String loginId;
    @Getter
    @Setter
    @Column
    @NotBlank
    private String password;
    @Getter
    @Setter
    @Column
    private boolean deleted;
}
