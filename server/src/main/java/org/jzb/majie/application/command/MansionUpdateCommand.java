package org.jzb.majie.application.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author jzb 2019-10-24
 */
@Data
public class MansionUpdateCommand implements Serializable {
    @NotBlank
    private String name;
}
