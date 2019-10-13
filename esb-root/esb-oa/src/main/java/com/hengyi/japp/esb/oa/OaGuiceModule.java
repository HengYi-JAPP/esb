package com.hengyi.japp.esb.oa;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataService;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmService;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;

import java.net.URL;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class OaGuiceModule extends AbstractModule {

    public static Injector OA_INJECTOR;

    synchronized public static void init(Vertx vertx) {
        if (OA_INJECTOR == null) {
            OA_INJECTOR = Guice.createInjector(new GuiceModule(vertx, "esb-oa"), new OaGuiceModule());
        }
    }

    @SneakyThrows
    @Provides
    @Singleton
    private WorkflowServicePortType WorkflowServicePortType(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject wsdl = vertxConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("WorkflowService"));
        final WorkflowService workflowService = new WorkflowService(url);
        return workflowService.getWorkflowServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    private BasicDataServicePortType BasicDataServicePortType(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject wsdl = vertxConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("BasicDataService"));
        final BasicDataService basicDataService = new BasicDataService(url);
        return basicDataService.getBasicDataServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    private HrmServicePortType HrmServicePortType(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject wsdl = vertxConfig.getJsonObject("wsdl");
        final URL url = new URL(wsdl.getString("HrmService"));
        final HrmService hrmService = new HrmService(url);
        return hrmService.getHrmServiceHttpPort();
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("restServiceConfig")
    private JsonObject restServiceConfig(@Named("vertxConfig") JsonObject vertxConfig) {
        return vertxConfig.getJsonObject("rest");
    }

}
