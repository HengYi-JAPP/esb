package com.hengyi.japp.esb.sap;

import com.google.inject.AbstractModule;
import com.hengyi.japp.esb.sap.interfaces.nnfp.NnfpService;
import com.hengyi.japp.esb.sap.interfaces.nnfp.internal.NnfpServiceImpl;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class SapGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(NnfpService.class).to(NnfpServiceImpl.class);
    }

//    @Provides
//    private HttpClient HttpClient() {
//        return vertx.createHttpClient();
//    }
//
//    @Provides
//    private WebClient WebClient() {
//        return WebClient.create(vertx);
//    }
//
//    @Provides
//    private Scheduler Scheduler() {
//        return RxHelper.scheduler(vertx);
//    }

}
