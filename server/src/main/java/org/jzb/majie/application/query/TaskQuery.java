package org.jzb.majie.application.query;

import com.github.ixtf.persistence.lucene.Jlucene;
import lombok.Builder;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.jzb.majie.domain.data.TaskStatus;
import org.jzb.majie.interfaces.lucene.LuceneService;
import org.jzb.majie.interfaces.lucene.internal.TaskLucene;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * @author jzb 2019-10-28
 */
@Data
@Builder
public class TaskQuery {
    private final int first;
    @Builder.Default
    private final int pageSize = 50;
    private final Principal principal;
    private final String taskGroupId;
    private final TaskStatus status;
    private final String mansionId;
    private final String q;

    public Mono<Pair<Long, List<String>>> rxQuery(LuceneService luceneService) {
        return Mono.fromCallable(() -> query(luceneService)).subscribeOn(Schedulers.elastic());
    }

    @SneakyThrows(IOException.class)
    public Pair<Long, List<String>> query(LuceneService luceneService) {
        final TaskLucene taskLucene = luceneService.get(TaskLucene.class);
        @Cleanup final IndexReader indexReader = taskLucene.indexReader();
        @Cleanup final SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        final BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        Jlucene.add(bqBuilder, "group", taskGroupId);
        Jlucene.add(bqBuilder, "status", status);
        Jlucene.add(bqBuilder, analyzer, q, "title", "content");
        final IndexSearcher searcher = new IndexSearcher(indexReader);
        final TopDocs topDocs = searcher.search(bqBuilder.build(), first + pageSize);
        return Pair.of(topDocs.totalHits, Jlucene.ids(searcher, topDocs.scoreDocs));
    }

}
