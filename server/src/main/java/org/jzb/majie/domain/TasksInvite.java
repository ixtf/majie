package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntityLoggable;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

/**
 * @author jzb 2019-10-24
 */
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class TasksInvite implements IEntityLoggable<Operator>, ITTL {
    // ticket
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
    @Size(min = 1)
    private Collection<Task> tasks;
    @Getter
    @Setter
    @Column
    @NotNull
    private Date expireDateTime;

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
