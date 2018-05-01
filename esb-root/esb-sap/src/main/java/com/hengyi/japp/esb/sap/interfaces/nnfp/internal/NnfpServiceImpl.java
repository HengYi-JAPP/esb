package com.hengyi.japp.esb.sap.interfaces.nnfp.internal;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.interfaces.nnfp.NnfpService;
import io.reactivex.Single;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.Properties;

/**
 * @author jzb 2018-03-13
 */
public class NnfpServiceImpl implements NnfpService {
    private final Properties nnfpConfig;
    private final WebClient webClient;

    @Inject
    NnfpServiceImpl(@Named("rootPath") String rootPath, WebClient webClient) {
        this.nnfpConfig = Util.readProperties(rootPath, "nnfp.config.properties");
        this.webClient = webClient;
    }

    @Override
    public Single<String> kpOrderSync(final String order) {
        final MultiMap form = MultiMap.caseInsensitiveMultiMap().set("order", DESDZFP.encrypt(order));
        return webClient.postAbs(nnfpConfig.getProperty("kpOrderSyncUrl"))
                .rxSendForm(form)
                .map(HttpResponse::bodyAsString);
    }

    @Override
    public Single<String> queryElectricKp(final String order) {
        final MultiMap form = MultiMap.caseInsensitiveMultiMap().set("order", DESDZFP.encrypt(order));
        return webClient.postAbs(nnfpConfig.getProperty("queryElectricKpUrl"))
                .rxSendForm(form)
                .map(HttpResponse::bodyAsString);
    }

}
