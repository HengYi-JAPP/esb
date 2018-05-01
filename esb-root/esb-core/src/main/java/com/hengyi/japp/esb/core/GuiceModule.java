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
    protected final Vertx vertx;

    protected GuiceModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Provides
    @Singleton
    @Named("vertxConfig")
    protected JsonObject vertxConfig() {
        return vertx.getOrCreateContext().config();
    }

    @Provides
    @Singleton
    @Named("rootPath")
    protected String rootPath() {
        return vertxConfig().getString("rootPath");
    }

    @Provides
    protected HttpClient HttpClient() {
        return vertx.createHttpClient();
    }

    @Provides
    protected WebClient WebClient() {
        return WebClient.create(vertx);
    }

    @Provides
    protected Scheduler Scheduler() {
        return RxHelper.scheduler(vertx);
    }

    @Provides
    @Singleton
    protected Vertx vertx() throws IOException {
        return vertx;
    }
}
