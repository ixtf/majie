package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntityLoggable;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

/**
 * @author jzb 2019-10-24
 */
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class TaskFeedback implements IEntityLoggable<Operator>, Comparable<TaskFeedback> {
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
    @NotBlank
    private String note;
    @Getter
    @Setter
    @Column
    private Collection<Attachment> attachments;

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

    @Override
    public int compareTo(TaskFeedback o) {
        return o.getModifyDateTime().compareTo(this.getModifyDateTime());
    }
}
