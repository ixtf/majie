package org.jzb.majie.application.command;

import com.github.ixtf.persistence.EntityDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @author jzb 2019-10-24
 */
@Data
public class TaskFeedbackUpdateCommand implements Serializable {
    @NotNull
    private EntityDTO task;
    @NotBlank
    private String content;
    private Set<EntityDTO> attachments;
}
