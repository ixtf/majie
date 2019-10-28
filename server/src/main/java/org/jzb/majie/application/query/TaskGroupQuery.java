package org.jzb.majie.application.query;

import com.github.ixtf.persistence.lucene.Jlucene;
import lombok.Builder;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.jzb.majie.domain.data.TaskStatus;
import org.jzb.majie.interfaces.lucene.LuceneService;
import org.jzb.majie.interfaces.lucene.internal.TaskLucene;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author jzb 2019-10-28
 */
@Data
@Builder
public class TaskGroupQuery {
    private final int first;
    @Builder.Default
    private final int pageSize = 50;
    private final Principal principal;
    @Builder.Default
    private final TaskStatus status = TaskStatus.RUN;
    private final String mansionId;

    public Mono<Pair<Long, List<String>>> rxQuery(LuceneService luceneService) {
        return Mono.fromCallable(() -> query(luceneService)).subscribeOn(Schedulers.elastic());
    }

    @SneakyThrows(IOException.class)
    public Pair<Long, List<String>> query(LuceneService luceneService) {
        final TaskLucene taskLucene = luceneService.get(TaskLucene.class);
        @Cleanup final IndexReader indexReader = taskLucene.indexReader();
        @Cleanup final DirectoryTaxonomyReader taxoReader = taskLucene.taxoReader();
        final BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        Jlucene.add(bqBuilder, "status", status);
        Jlucene.add(bqBuilder, "mansion", mansionId);
        final FacetsCollector fc = new FacetsCollector();
        final IndexSearcher searcher = new IndexSearcher(indexReader);
        searcher.search(bqBuilder.build(), fc);
        final Facets facets = new FastTaxonomyFacetCounts("group", taxoReader, taskLucene.facetsConfig(), fc);
        final FacetResult facetResult = facets.getTopChildren(Integer.MAX_VALUE, "group");
        if (ArrayUtils.isEmpty(facetResult.labelValues)) {
            return Pair.of(0l, List.of());
        }
        final List<String> ids = Arrays.stream(facetResult.labelValues)
                .map(it -> it.label)
                .collect(toList());
        return Pair.of(Long.valueOf(ids.size()), ids);
    }
}
