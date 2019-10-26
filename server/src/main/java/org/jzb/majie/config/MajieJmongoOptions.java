package org.jzb.majie.config;

import com.github.ixtf.persistence.mongo.JmongoOptions;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.vertx.core.json.JsonObject;
import org.jzb.majie.MajieModule;

/**
 * @author jzb 2019-10-24
 */
public class MajieJmongoOptions extends JmongoOptions {
    @Override
    protected MongoClient client() {
        final Named named = Names.named("vertxConfig");
        final Key<JsonObject> key = Key.get(JsonObject.class, named);
        final JsonObject vertxConfig = MajieModule.getInstance(key);
        final JsonObject config = vertxConfig.getJsonObject("mongo");
        final String connection_string = config.getString("connection_string");
        final MongoClientSettings.Builder builder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connection_string));
        return MongoClients.create(builder.build());
    }

    @Override
    public String dbName() {
        return "majie";
    }
}
