package org.jzb.majie.interfaces.lucene.internal;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.lucene.Jlucene;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.jzb.majie.domain.Task;
import org.jzb.majie.domain.TaskGroup;

import java.util.Optional;

/**
 * @author jzb 2019-10-25
 */
@Slf4j
@Singleton
public class TaskLucene extends BaseLucene<Task> {

    @Inject
    private TaskLucene(Jmongo jmongo) {
        super(jmongo);
    }

    @Override
    public FacetsConfig facetsConfig() {
        final FacetsConfig result = new FacetsConfig();
        result.setIndexFieldName("mansion", "mansion");
        result.setIndexFieldName("group", "group");
        result.setMultiValued("tags", true);
        result.setIndexFieldName("tags", "tags");
        result.setMultiValued("relate_operator", true);
        result.setIndexFieldName("relate_operator", "relate_operator");
        return result;
    }

    @Override
    protected Document document(Task o) {
        final Document doc = new Document();
        doc.add(new StringField("id", o.getId(), Field.Store.YES));

        final String groupId = Optional.ofNullable(o.getGroup())
                .map(TaskGroup::getId)
                .filter(J::nonBlank)
                .orElse("0");
        doc.add(new StringField("group", groupId, Field.Store.YES));
        doc.add(new FacetField("group", groupId));

        Jlucene.addTextField(doc, "title", o.getTitle());
        Jlucene.addTextField(doc, "content", o.getContent());
        return doc;
    }

}
