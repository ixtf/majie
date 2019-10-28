package org.jzb.majie.interfaces.lucene.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.Util;
import org.jzb.majie.interfaces.lucene.LuceneService;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

/**
 * @author jzb 2019-10-25
 */
@Slf4j
@Singleton
public class LuceneServiceImpl implements LuceneService {
    private final Collection<BaseLucene> lucenes;

    @Inject
    private LuceneServiceImpl() {
        lucenes = Util.collectSubInstance(BaseLucene.class);
    }

    @Override
    public <T extends BaseLucene> T get(Class<T> clazz) {
        return lucenes.parallelStream()
                .filter(it -> it.getClass() == clazz)
                .findFirst()
                .map(clazz::cast)
                .orElseThrow();
    }

    @Override
    public Mono<Void> index(String clazz, String id) {
        final BaseLucene lucene = lucenes.parallelStream()
                .filter(it -> Objects.equals(it.getEntityClass().getName(), clazz))
                .findFirst()
                .orElse(null);
        return lucene == null ? Mono.empty() : lucene.index(id);
    }

    @Override
    public Mono<Void> remove(String clazz, String id) {
        final BaseLucene lucene = lucenes.parallelStream()
                .filter(it -> Objects.equals(it.getEntityClass().getName(), clazz))
                .findFirst()
                .orElse(null);
        return lucene == null ? Mono.empty() : lucene.remove(id);
    }
}
