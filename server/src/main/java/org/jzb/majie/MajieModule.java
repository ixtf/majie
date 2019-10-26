package org.jzb.majie;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.mongo.Jmongo;
import com.google.common.io.Resources;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.SneakyThrows;
import org.jzb.majie.config.MajieJmongoOptions;
import org.jzb.majie.interfaces.graphql.DataFetchers;
import org.jzb.weixin.mp.MpClient;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.github.ixtf.japp.core.Constant.YAML_MAPPER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

//import org.jzb.weixin.mp.MpAccessToken;

/**
 * @author jzb 2019-10-24
 */
public class MajieModule extends AbstractModule {
    private static Injector INJECTOR;
    private final Vertx vertx;

    private MajieModule(Vertx vertx) {
        this.vertx = vertx;
    }

    synchronized public static void init(Vertx vertx) {
        if (INJECTOR == null) {
            INJECTOR = Guice.createInjector(new MajieModule(vertx));
        }
    }

    public static <T> T getInstance(Class<T> clazz) {
        return INJECTOR.getInstance(clazz);
    }

    public static <T> T getInstance(Key<T> key) {
        return INJECTOR.getInstance(key);
    }

    public static void injectMembers(Object o) {
        INJECTOR.injectMembers(o);
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
    }

    @Provides
    @Singleton
    @Named("rootPath")
    private Path rootPath() {
        return Paths.get(System.getProperty("majie.path", "/home/majie"));
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("vertxConfig")
    private JsonObject vertxConfig(@Named("rootPath") Path rootPath) {
        final File ymlFile = rootPath.resolve("config.yml").toFile();
        if (ymlFile.exists()) {
            final Map map = YAML_MAPPER.readValue(ymlFile, Map.class);
            return new JsonObject(map);
        }
        final File jsonFile = rootPath.resolve("config.json").toFile();
        final Map map = MAPPER.readValue(jsonFile, Map.class);
        return new JsonObject(map);
    }

    @Provides
    @Singleton
    private MpClient MpClient(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("weixin-mp");
        final Properties properties = new Properties();
        properties.putAll(config.getMap());
        return MpClient.getInstance(properties);
    }

    @Provides
    @Singleton
    private MongoClient MongoClient(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("mongo");
        final String host = config.getString("host");
        final StringBuilder sb = new StringBuilder("mongodb://").append(host);
        final Collection<String> excludes = Set.of("host", "port");
        final String params = config.getMap().entrySet().stream()
                .filter(entry -> !excludes.contains(entry.getKey()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(joining("&"));
        if (J.nonBlank(params)) {
            sb.append("/?").append(params);
        }
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(sb.toString()))
                .build());
    }

    @Provides
    @Singleton
    private Jmongo Jmongo() {
        return Jmongo.of(MajieJmongoOptions.class);
    }

    // openssl ecparam -name prime256v1 -genkey -out private.pem
    // openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_key.pem
    // openssl ec -in private.pem -pubout -out public.pem
    @Provides
    @Singleton
    private PubSecKeyOptions PubSecKeyOptions(@Named("vertxConfig") JsonObject vertxConfig) {
        return new PubSecKeyOptions(vertxConfig.getJsonObject("jwt"));
    }

    @Provides
    @Singleton
    private JWTAuth JWTAuth(PubSecKeyOptions pubSecKeyOptions) {
        final JWTAuthOptions jwtAuthOptions = new JWTAuthOptions().addPubSecKey(pubSecKeyOptions);
        return JWTAuth.create(vertx, jwtAuthOptions);
    }

    @Provides
    @Singleton
    private GraphQL GraphQL() {
        final SchemaParser schemaParser = new SchemaParser();
        final TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        Stream.of("majie.graphql", "majie-command.graphql", "majie-type.graphql")
                .map(this::loadSdl)
                .map(schemaParser::parse)
                .forEach(typeDefinitionRegistry::merge);
        final SchemaGenerator schemaGenerator = new SchemaGenerator();
        final RuntimeWiring runtimeWiring = DataFetchers.buildWiring();
        final GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    @SneakyThrows
    private String loadSdl(String fileName) {
        final URL url = Resources.getResource(fileName);
        return Resources.toString(url, UTF_8);
    }
}
