package com.hengyi.japp.esb.sap;

import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.sap.interfaces.nnfp.NnfpService;
import com.hengyi.japp.esb.sap.interfaces.nnfp.internal.NnfpServiceImpl;
import io.vertx.reactivex.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceSapModule extends GuiceModule {

    protected GuiceSapModule(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void configure() {
        bind(NnfpService.class).to(NnfpServiceImpl.class);
    }

}
