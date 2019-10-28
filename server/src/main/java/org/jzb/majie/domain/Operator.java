package org.jzb.majie.domain;

import com.github.ixtf.persistence.IOperator;
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
public class Operator implements IOperator {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Id
    @NotBlank
    private String id;
    /**
     * 默认
     */
    @Getter
    @Setter
    @Column
    private Mansion mansion;
    @ToString.Include
    @Getter
    @Setter
    @Column
    @NotBlank
    private String name;
    @Getter
    @Setter
    @Column
    private boolean admin;
    @Getter
    @Setter
    @Column
    private boolean deleted;
}
