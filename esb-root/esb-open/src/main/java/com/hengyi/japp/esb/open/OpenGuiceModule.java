package com.hengyi.japp.esb.open;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
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

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class OpenGuiceModule extends AbstractModule {

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
    private Config Config(@Named("vertxConfig") JsonObject vertxConfig, CasClient casClient) {
        final String baseUrl = vertxConfig.getString("baseUrl", "http://localhost:9999");
        final Clients clients = new Clients(baseUrl + "/callback", casClient);
        final Config config = new Config(clients);
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        return config;
    }

    @Provides
    @Singleton
    private CasClient CasClient(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig) {
        final String casUrl = vertxConfig.getString("casUrl", "http://cas.hengyi.com:8080/login");
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

}
