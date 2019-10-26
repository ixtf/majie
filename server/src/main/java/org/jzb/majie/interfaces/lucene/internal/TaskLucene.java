package org.jzb.majie.interfaces.lucene.internal;

import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.jzb.majie.domain.Task;

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
    protected FacetsConfig facetsConfig() {
        return null;
    }

    @Override
    protected Document document(Task o) {
        return null;
    }

}
