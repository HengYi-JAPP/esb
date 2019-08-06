package com.hengyi.japp.esb.oa;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataService;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmService;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Tracer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import lombok.SneakyThrows;

import java.net.URL;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceOaModule extends GuiceModule {

    protected GuiceOaModule(Vertx vertx) {
        super(vertx);
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("esb-oa-config")
    private JsonObject EsbOaConfig(@Named("esb-config") JsonObject esbConfig) {
        return Util.readJsonObject(esbConfig.getString("rootPath"), "esb-oa", "config.json");
    }

    @SneakyThrows
    @Provides
    @Singleton
    private WorkflowServicePortType WorkflowServicePortType(@Named("esb-oa-config") JsonObject esbOaConfig) {
        final JsonObject wsdl = esbOaConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("WorkflowService"));
        final WorkflowService workflowService = new WorkflowService(url);
        return workflowService.getWorkflowServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    private BasicDataServicePortType BasicDataServicePortType(@Named("esb-oa-config") JsonObject esbOaConfig) {
        final JsonObject wsdl = esbOaConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("BasicDataService"));
        final BasicDataService basicDataService = new BasicDataService(url);
        return basicDataService.getBasicDataServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    private HrmServicePortType HrmServicePortType(@Named("esb-oa-config") JsonObject esbOaConfig) {
        final JsonObject wsdl = esbOaConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("HrmService"));
        final HrmService hrmService = new HrmService(url);
        return hrmService.getHrmServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    private Tracer Tracer(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject apm = vertxConfig.getJsonObject("apm");
        final SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        final SenderConfiguration senderConfiguration = new SenderConfiguration().withAgentHost(apm.getString("agentHost"));
        final ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withSender(senderConfiguration).withLogSpans(true);
        final Configuration config = new Configuration("esb-oa").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
