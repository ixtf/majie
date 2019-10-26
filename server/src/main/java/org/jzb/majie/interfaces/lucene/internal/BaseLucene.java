package org.jzb.majie.interfaces.lucene.internal;

import com.github.ixtf.persistence.IEntity;
import com.github.ixtf.persistence.mongo.Jmongo;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.jzb.majie.Util;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;

/**
 * @author jzb 2019-10-25
 */
public abstract class BaseLucene<T extends IEntity> {
    @Getter
    protected final Class<T> entityClass;
    protected final IndexWriter indexWriter;
    protected final DirectoryTaxonomyWriter taxoWriter;
    protected final FacetsConfig facetsConfig;
    protected final Jmongo jmongo;

    @SneakyThrows(IOException.class)
    protected BaseLucene( Jmongo jmongo) {
        this.jmongo = jmongo;
        entityClass = entityClass();
        final Path indexPath = Util.luceneIndexPath(entityClass);
        indexWriter = new IndexWriter(FSDirectory.open(indexPath), new IndexWriterConfig(new SmartChineseAnalyzer()));
        final Path taxoPath = Util.luceneTaxoPath(entityClass);
        taxoWriter = new DirectoryTaxonomyWriter(FSDirectory.open(taxoPath));
        facetsConfig = facetsConfig();
    }

    public Mono<Void> index(String id) {
        return jmongo.find(entityClass, id).flatMap(this::index);
    }

    public Mono<Void> index(T entity) {
        return Mono.fromCallable(() -> {
            final Term term = new Term("id", entity.getId());
            if (entity.isDeleted()) {
                indexWriter.deleteDocuments(term);
            } else {
                indexWriter.updateDocument(term, facetsConfig.build(taxoWriter, document(entity)));
            }
            indexWriter.commit();
            taxoWriter.commit();
            return true;
        }).then();
    }

    public Mono<Void> remove(String id) {
        return Mono.fromCallable(() -> {
            final Term term = new Term("id", id);
            indexWriter.deleteDocuments(term);
            indexWriter.commit();
            taxoWriter.commit();
            return true;
        }).then();
    }

    private Class<T> entityClass() {
        final ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public void close() throws IOException {
        taxoWriter.close();
        indexWriter.close();
    }

    protected abstract FacetsConfig facetsConfig();

    protected abstract Document document(T entity);
}
