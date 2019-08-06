package com.hengyi.japp.esb.agent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.AbstractVerticle;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends AbstractVerticle {
    public static Injector GUICE;

    @Override
    public Completable rxStart() {
        GUICE = Guice.createInjector(new GuiceAgentModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return Completable.mergeArray(
                vertx.rxDeployVerticle(AuthVerticle.class.getName(), deploymentOptions).ignoreElement()
        );
    }

}
