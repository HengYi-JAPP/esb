package com.hengyi.japp.esb.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Tracer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
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
    @Named("vertxConfig")
    private JsonObject vertxConfig() {
        return vertx.getOrCreateContext().config();
    }

    @Provides
    @Singleton
    @Named("rootPath")
    private String rootPath(@Named("vertxConfig") JsonObject vertxConfig) {
        return vertxConfig.getString("rootPath");
    }


    @SneakyThrows
    @Provides
    @Singleton
    private Tracer Tracer(@com.google.inject.name.Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject apm = vertxConfig.getJsonObject("apm");
        final String serviceName = apm.getString("serviceName");
        final String agentHost = apm.getString("agentHost");
        final SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
//        final SenderConfiguration senderConfiguration = new SenderConfiguration().withAgentHost(agentHost);
        final SenderConfiguration senderConfiguration = new SenderConfiguration().withEndpoint("http://" + agentHost + ":14268/api/traces");
        final ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withSender(senderConfiguration).withLogSpans(true);
        final Configuration config = new Configuration(serviceName).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

    @Provides
    @Singleton
    private Vertx vertx() {
        return vertx;
    }
}
