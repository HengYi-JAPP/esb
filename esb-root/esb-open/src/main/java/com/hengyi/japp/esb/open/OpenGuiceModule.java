package com.hengyi.japp.esb.open;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.open.config.YunbiaoMailConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.sstore.LocalSessionStore;
import lombok.SneakyThrows;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.vertx.auth.Pac4jAuthProvider;
import org.pac4j.vertx.cas.logout.VertxCasLogoutHandler;
import org.pac4j.vertx.context.session.VertxSessionStore;
import org.pac4j.vertx.core.store.VertxLocalMapStore;
import org.pac4j.vertx.http.DefaultHttpActionAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class OpenGuiceModule extends AbstractModule {
    private static Injector INJECTOR;

    synchronized public static void init(Vertx vertx) {
        if (INJECTOR != null) {
            return;
        }
        INJECTOR = Guice.createInjector(new GuiceModule(vertx, "esb-open"), new OpenGuiceModule());
    }

    public static <T> T getInstance(Class<T> clazz) {
        return INJECTOR.getInstance(clazz);
    }

    public static <T> T getInstance(Key<T> key) {
        return INJECTOR.getInstance(key);
    }

    @Provides
    @Singleton
    @Named("pac4jConfig")
    private JsonObject pac4jConfig(@Named("vertxConfig") JsonObject vertxConfig) {
        return vertxConfig.getJsonObject("pac4j", new JsonObject());
    }

    @Provides
    @Singleton
    private Pac4jAuthProvider Pac4jAuthProvider() {
        return new Pac4jAuthProvider();
    }

    @Provides
    @Singleton
    private SessionStore VertxSessionStore(LocalSessionStore localSessionStore) {
        return new VertxSessionStore(localSessionStore);
    }

    @Provides
    @Singleton
    private LocalSessionStore LocalSessionStore(Vertx vertx) {
        return LocalSessionStore.create(vertx);
    }

    @Provides
    @Singleton
    private Config Config(@Named("pac4jConfig") JsonObject pac4jConfig, CasClient casClient) {
        final String baseUrl = pac4jConfig.getString("baseUrl", "http://localhost:9999");
        final Clients clients = new Clients(baseUrl + "/callback", casClient);
        final Config config = new Config(clients);
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        return config;
    }

    @Provides
    @Singleton
    private CasClient CasClient(Vertx vertx, @Named("pac4jConfig") JsonObject pac4jConfig) {
        final String casUrl = pac4jConfig.getString("casUrl", "http://cas.hengyi.com:8080/login");
        final CasConfiguration casConfiguration = new CasConfiguration(casUrl);
        casConfiguration.setLogoutHandler(new VertxCasLogoutHandler(new VertxLocalMapStore(vertx), false));
        return new CasClient(casConfiguration);
    }

    @SneakyThrows
    @Provides
    @Singleton
    private KeyStore KeyStore(@Named("rootPath") Path rootPath) {
        final KeyStore keystore = KeyStore.getInstance("jceks");
        final File file = rootPath.resolve("esb-open.jceks").toFile();
        keystore.load(new FileInputStream(file), "esb-open-tomking".toCharArray());
        return keystore;
    }

    @SneakyThrows
    @Provides
    @Singleton
    private PrivateKey PrivateKey(KeyStore keystore) {
        return (PrivateKey) keystore.getKey("esb-open", "esb-open-tomking".toCharArray());
    }

    @SneakyThrows
    @Provides
    @Singleton
    private PublicKey PublicKey(KeyStore keystore) {
        return keystore.getCertificate("esb-open").getPublicKey();
    }

    @Provides
    @Singleton
    private YunbiaoMailConfig YunbiaoMailConfig(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("yunbiao-mail", new JsonObject());
        final String host = config.getString("host", "smtp.exmail.qq.com");
        final String from = config.getString("from");
        final String password = config.getString("password");
        final String subject = config.getString("subject", "恒逸云表密码修改");
        final String urlTpl = config.getString("urlTpl", "http://www.baidu.com?mailKey=${mailKey}");
        final String contentTpl = config.getString("contentTpl", "<h1>修改密码</h1><hr>${url}");
        final String checkSql = config.getString("checkSql", "select f12,f44 from T10001_C2778 where f12=? and f44=?");
        final String updateSql = config.getString("updateSql", "update LATO_USER set M_PASSWORD=?,M_SALT='' where M_ACCOUNT=?");
        return new YunbiaoMailConfig(host, from, password, subject, urlTpl, contentTpl, checkSql, updateSql);
    }

    @SneakyThrows
    @Provides
    @Named("yunbiao_DS")
    private JDBCClient yunbiao_DS(Vertx vertx, @Named("rootPath") Path rootPath) {
        final Path path = rootPath.resolve("yunbiao_DS.config.json");
        final Map map = MAPPER.readValue(path.toFile(), Map.class);
        final JsonObject config = new JsonObject(map);
        return JDBCClient.createShared(vertx, config, "yunbiao_DS");
    }

}
