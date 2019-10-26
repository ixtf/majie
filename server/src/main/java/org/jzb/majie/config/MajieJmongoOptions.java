package org.jzb.majie.config;

import com.github.ixtf.persistence.mongo.JmongoOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import org.jzb.majie.MajieModule;

/**
 * @author jzb 2019-10-24
 */
public class MajieJmongoOptions extends JmongoOptions {
    @Override
    protected MongoClient client() {
        return MajieModule.getInstance(MongoClient.class);
    }

    @Override
    public String dbName() {
        return "majie";
    }
}
