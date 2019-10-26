package org.jzb.majie.interfaces.lucene.internal;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jzb.majie.MajieModule;
import org.jzb.majie.interfaces.lucene.LuceneService;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toSet;

/**
 * @author jzb 2019-10-25
 */
@Slf4j
@Singleton
public class LuceneServiceImpl implements LuceneService {
    private final Collection<BaseLucene> lucenes;

    @SneakyThrows(IOException.class)
    @Inject
    private LuceneServiceImpl() {
        lucenes = ClassPath.from(Thread.currentThread().getContextClassLoader())
                .getTopLevelClasses(this.getClass().getPackageName())
                .parallelStream()
                .map(ClassPath.ClassInfo::load)
                .filter(BaseLucene.class::isAssignableFrom)
                .filter(it -> it != BaseLucene.class)
                .map(MajieModule::getInstance)
                .map(BaseLucene.class::cast)
                .collect(toSet());
    }

    @Override
    public Mono<Void> index(String clazz, String id) {
        final BaseLucene lucene = lucenes.stream()
                .filter(it -> Objects.equals(it.getEntityClass().getName(), clazz))
                .findFirst()
                .orElse(null);
        return lucene == null ? Mono.empty() : lucene.index(id);
    }

    @Override
    public Mono<Void> remove(String clazz, String id) {
        final BaseLucene lucene = lucenes.stream()
                .filter(it -> Objects.equals(it.getEntityClass().getName(), clazz))
                .findFirst()
                .orElse(null);
        return lucene == null ? Mono.empty() : lucene.remove(id);
    }
}
