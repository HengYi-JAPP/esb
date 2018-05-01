package com.hengyi.japp.esb.open.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.sdk.config.BasePac4jConfigurationFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-03-26
 */
public class Pac4jConfigurationFactory extends BasePac4jConfigurationFactory {
    private JsonObject config;

    @Inject
    Pac4jConfigurationFactory(@Named("authConfig") JsonObject config, Vertx vertx) {
        super(vertx);
        this.config = config;
    }

    @Override
    protected JsonObject jsonConf() {
        return config;
    }
}
