package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntityLoggable;
import lombok.*;
import org.jzb.majie.domain.listener.LuceneListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author jzb 2019-10-24
 */
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(LuceneListener.class)
@Entity
public class TaskGroup implements IEntityLoggable<Operator> {
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
    @NotNull
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
    @NotNull
    private Operator creator;
    @Getter
    @Setter
    @Column(name = "cdt")
    @NotNull
    private Date createDateTime;
    @Getter
    @Setter
    @Column
    private Operator modifier;
    @Getter
    @Setter
    @Column(name = "mdt")
    private Date modifyDateTime;
    @Getter
    @Setter
    @Column
    private boolean deleted;
}
