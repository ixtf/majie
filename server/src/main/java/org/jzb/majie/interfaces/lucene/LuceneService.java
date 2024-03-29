package org.jzb.majie.interfaces.lucene;

import com.google.inject.ImplementedBy;
import org.jzb.majie.interfaces.lucene.internal.BaseLucene;
import org.jzb.majie.interfaces.lucene.internal.LuceneServiceImpl;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2019-10-25
 */
@ImplementedBy(LuceneServiceImpl.class)
public interface LuceneService {

    <T extends BaseLucene> T get(Class<T> clazz);

    Mono<Void> index(String clazz, String id);

    Mono<Void> remove(String clazz, String id);
}
