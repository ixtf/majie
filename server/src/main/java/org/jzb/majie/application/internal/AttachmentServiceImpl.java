package org.jzb.majie.application.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.github.ixtf.persistence.mongo.MongoUnitOfWork;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jzb.majie.Util;
import org.jzb.majie.application.AttachmentService;
import org.jzb.majie.application.QueryService;
import org.jzb.majie.domain.Attachment;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;

/**
 * @author jzb 2019-10-24
 */
@Slf4j
@Singleton
public class AttachmentServiceImpl implements AttachmentService {
    private final Jmongo jmongo;

    @Inject
    private AttachmentServiceImpl(Jmongo jmongo) {
        this.jmongo = jmongo;
    }

    @Override
    public Mono<Attachment> handleUpload(Principal principal, String uploadedFileName, Attachment attachment) {
        return Mono.fromCallable(() -> DigestUtils.sha256Hex(new FileInputStream(uploadedFileName)))
                .flatMap(sha256Hex -> jmongo.find(Attachment.class, sha256Hex).switchIfEmpty(createAttachment(principal, uploadedFileName, attachment, sha256Hex)));
    }

    private Mono<Attachment> createAttachment(Principal principal, String uploadedFileName, Attachment attachment, String sha256Hex) {
        final MongoUnitOfWork uow = jmongo.uow();
        uow.registerNew(attachment);
        attachment.setId(sha256Hex);
        return QueryService.find(jmongo, principal).flatMap(operator -> {
            attachment.log(operator);
            moveFile(uploadedFileName, attachment);
            return uow.rxCommit().thenReturn(attachment);
        });
    }

    @SneakyThrows(IOException.class)
    private void moveFile(String uploadedFileName, Attachment attachment) {
        final File srcFile = FileUtils.getFile(uploadedFileName);
        final File destFile = Util.file(attachment);
        FileUtils.moveFile(srcFile, destFile);
    }
}
