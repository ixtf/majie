package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * @author jzb 2019-10-24
 */
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class TaskNotice implements IEntity {
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
    private Task task;
    @Getter
    @Setter
    @Column
    @Size(min = 1)
    private Collection<Operator> receivers;
    @Getter
    @Setter
    @Column
    @NotBlank
    private String content;
    @Getter
    @Setter
    @Column
    private boolean noticed;
    @Getter
    @Setter
    @Column
    private boolean deleted;
}
