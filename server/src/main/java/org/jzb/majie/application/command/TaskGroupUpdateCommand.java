package org.jzb.majie.application.command;

import com.github.ixtf.persistence.EntityDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jzb 2019-10-24
 */
@Data
public class TaskGroupUpdateCommand implements Serializable {
    @NotNull
    private EntityDTO mansion;
    @NotBlank
    private String name;
}
