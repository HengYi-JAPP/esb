package com.hengyi.japp.esb.open.application.internal;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;

/**
 * @author jzb 2019-09-28
 */
@Singleton
public class Pac4jConfigurationFactory implements ConfigFactory {
    private final Vertx vertx;
    private final JsonObject vertxConfig;
    private final CasClient casClient;

    private Pac4jConfigurationFactory(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig, CasClient casClient) {
        this.vertx = vertx;
        this.vertxConfig = vertxConfig;
        this.casClient = casClient;
    }

    @Override
    public Config build(Object... parameters) {
        final String baseUrl = vertxConfig.getString("baseUrl", "http://localhost:9999");
        final Clients clients = new Clients(baseUrl + "/pac4j/callback", casClient);
        return new Config(clients);
    }
}
