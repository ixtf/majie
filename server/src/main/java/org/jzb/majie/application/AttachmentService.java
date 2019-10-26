package org.jzb.majie.application;

import com.google.inject.ImplementedBy;
import org.jzb.majie.application.internal.AttachmentServiceImpl;
import org.jzb.majie.domain.Attachment;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author jzb 2019-10-25
 */
@ImplementedBy(AttachmentServiceImpl.class)
public interface AttachmentService {

    Mono<Attachment> handleUpload(Principal principal, String uploadedFileName, Attachment attachment);

}
