package com.hengyi.japp.esb.sap.application.internal;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.application.NnfpService;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Properties;

/**
 * @author jzb 2018-03-13
 */
public class NnfpServiceImpl implements NnfpService {
    private final Properties nnfpConfig;
    private final WebClient webClient;

    @Inject
    NnfpServiceImpl(Vertx vertx, @Named("rootPath") String rootPath) {
        this.nnfpConfig = Util.readProperties(rootPath, "nnfp.config.properties");
        this.webClient = WebClient.create(vertx);
    }

    @Override
    public Future<String> kpOrderSync(final String order) {
        return Future.future(promise -> {
            final MultiMap form = MultiMap.caseInsensitiveMultiMap().set("order", DESDZFP.encrypt(order));
            webClient.postAbs(nnfpConfig.getProperty("kpOrderSyncUrl"))
                    .sendForm(form, ar -> promise.handle(ar.map(HttpResponse::bodyAsString)));
        });
    }

    @Override
    public Future<String> queryElectricKp(final String order) {
        return Future.future(promise -> {
            final MultiMap form = MultiMap.caseInsensitiveMultiMap().set("order", DESDZFP.encrypt(order));
            webClient.postAbs(nnfpConfig.getProperty("kpOrderSyncUrl"))
                    .sendForm(form, ar -> promise.handle(ar.map(HttpResponse::bodyAsString)));
        });
    }

}
