package com.hengyi.japp.esb.oa;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataService;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmService;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
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
}
