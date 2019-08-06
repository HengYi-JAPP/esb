package com.hengyi.japp.esb.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Tracer;
import io.reactivex.Scheduler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.ext.web.client.WebClient;
import lombok.SneakyThrows;

import javax.inject.Named;

/**
 * @author jzb 2018-03-21
 */
public class GuiceModule extends AbstractModule {
    private final Vertx vertx;

    public GuiceModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Provides
    @Singleton
    @Named("esb-config")
    private JsonObject esbConfig() {
        return Util.readJsonObject("/home/esb", "config.json");
    }

    @SneakyThrows
    @Provides
    @Singleton
    private Tracer Tracer(@Named("esb-config") JsonObject esbConfig) {
        final JsonObject apm = esbConfig.getJsonObject("apm");
        final SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        final SenderConfiguration senderConfiguration = new SenderConfiguration().withAgentHost(apm.getString("agentHost"));
        final ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withSender(senderConfiguration).withLogSpans(true);
        final Configuration config = new Configuration("esb-oa").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
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
    private Vertx vertx() {
        return vertx;
    }
}
