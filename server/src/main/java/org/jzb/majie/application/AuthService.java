package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import io.vertx.core.Future;
import lombok.Data;
import org.jzb.majie.application.command.TokenCommand;
import org.jzb.majie.application.internal.AuthServiceImpl;
import org.jzb.majie.domain.Attachment;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.Serializable;
import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@ImplementedBy(AuthServiceImpl.class)
public interface AuthService {

    Mono<String> token(TokenCommand command);

    Future<DownloadFile> downloadFile(String token);

    String downloadToken(Principal principal, Attachment attachment);

    @Data
    class DownloadFile implements Serializable {
        private String filePath;
        private String fileName;

        public void setFile(File file) {
            filePath = file.getPath();
        }
    }
}
