package com.hengyi.japp.esb.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Tracer;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;

import javax.inject.Named;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.github.ixtf.japp.core.Constant.YAML_MAPPER;

/**
 * @author jzb 2018-03-21
 */
public class GuiceModule extends AbstractModule {
    private final static String ESB_HOME = System.getProperty("JAPP_ESB_HOME", "/home/esb");
    private final Vertx vertx;
    private final String module;

    public GuiceModule(Vertx vertx, String module) {
        this.vertx = vertx;
        this.module = module;
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
    }

    @Provides
    @Singleton
    @Named("rootPath")
    private Path rootPath() {
        return Paths.get(ESB_HOME, module);
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("vertxConfig")
    private JsonObject vertxConfig(@Named("rootPath") Path rootPath) {
        final Map map;
        final File ymlFile = rootPath.resolve("config.yml").toFile();
        final File jsonFile = rootPath.resolve("config.json").toFile();
        if (ymlFile.exists()) {
            map = YAML_MAPPER.readValue(ymlFile, Map.class);
        } else {
            map = MAPPER.readValue(jsonFile, Map.class);
        }
        return new JsonObject(map);
    }

    @SneakyThrows
    @Provides
    @Singleton
    private Tracer Tracer(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject apm = vertxConfig.getJsonObject("apm");
        final String serviceName = apm.getString("serviceName");
        final String agentHost = apm.getString("agentHost");
        final SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        final SenderConfiguration senderConfiguration = new SenderConfiguration().withEndpoint("http://" + agentHost + ":14268/api/traces");
        final ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withSender(senderConfiguration).withLogSpans(true);
        final Configuration config = new Configuration(serviceName).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

}
