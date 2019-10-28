package org.jzb.majie.domain;

import com.github.ixtf.persistence.IEntityLoggable;
import lombok.*;
import org.jzb.majie.domain.data.TaskStatus;
import org.jzb.majie.domain.listener.LuceneListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@EntityListeners(LuceneListener.class)
@Entity
public class Task implements IEntityLoggable<Operator> {
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
    private TaskGroup group;
    @ToString.Include
    @Getter
    @Setter
    @Column
    @NotBlank
    private String title;
    @Getter
    @Setter
    @Column
    @NotBlank
    private String content;
    @Getter
    @Setter
    @Column
    @NotNull
    private TaskStatus status;
    @Getter
    @Setter
    @Column
    private Collection<Attachment> attachments;
    @Getter
    @Setter
    @Column
    private Collection<Operator> chargers;
    @Getter
    @Setter
    @Column
    private Collection<Operator> participants;

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
