package com.hengyi.japp.esb.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.reactivex.Scheduler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.ext.web.client.WebClient;

import javax.inject.Named;
import java.io.IOException;

/**
 * @author jzb 2018-03-21
 */
public abstract class GuiceModule extends AbstractModule {
    private final Vertx vertx;

    protected GuiceModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Provides
    @Singleton
    @Named("vertxConfig")
    private JsonObject vertxConfig() {
        return vertx.getOrCreateContext().config();
    }

    @Provides
    @Singleton
    @Named("rootPath")
    private String rootPath() {
        return vertxConfig().getString("rootPath");
    }

    @Provides
    private HttpClient HttpClient() {
        return vertx.createHttpClient();
    }

    @Provides
    private WebClient WebClient() {
        return WebClient.create(vertx);
    }

    @Provides
    private Scheduler Scheduler() {
        return RxHelper.scheduler(vertx);
    }

    @Provides
    @Singleton
    private Vertx vertx() throws IOException {
        return vertx;
    }
}
