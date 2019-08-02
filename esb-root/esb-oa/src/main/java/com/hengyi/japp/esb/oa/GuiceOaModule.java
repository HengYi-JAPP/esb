package com.hengyi.japp.esb.oa;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
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
    private WorkflowServicePortType WorkflowServicePortType() {
        final URL url = new URL("http://220.189.213.71:8077//services/WorkflowService?wsdl");
        final WorkflowService workflowService = new WorkflowService(url);
        return workflowService.getWorkflowServiceHttpPort();
    }
}
