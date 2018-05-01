package com.hengyi.japp.esb.core.verticle;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.sstore.ClusteredSessionStore;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.vertx.VertxWebContext;
import org.pac4j.vertx.auth.Pac4jAuthProvider;
import org.pac4j.vertx.context.session.VertxSessionStore;
import org.pac4j.vertx.handler.impl.CallbackHandler;
import org.pac4j.vertx.handler.impl.CallbackHandlerOptions;
import org.pac4j.vertx.handler.impl.SecurityHandler;
import org.pac4j.vertx.handler.impl.SecurityHandlerOptions;
import org.pac4j.vertx.http.DefaultHttpActionAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jzb 2018-03-27
 */
public class BaseRestAPIVerticle extends BaseVerticle {
    protected final Pac4jAuthProvider authProvider = new Pac4jAuthProvider();
    protected SessionStore vertxSessionStore;
    protected Config config = null;
    protected org.pac4j.core.context.session.SessionStore<VertxWebContext> sessionStore;

    protected void enablePac4j(ConfigFactory configFactory, final Router router) {
        config = configFactory.build();
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        sessionStore = new VertxSessionStore(vertxSessionStore.getDelegate());
        router.route().handler(UserSessionHandler.create(new AuthProvider(authProvider)));
        final CallbackHandlerOptions callbackHandlerOptions = new CallbackHandlerOptions().setDefaultUrl("/").setMultiProfile(true);
        final CallbackHandler callbackHandler = new CallbackHandler(vertx.getDelegate(), sessionStore, config, callbackHandlerOptions);
        router.getDelegate().get("/callback").handler(callbackHandler);
        router.post("/callback").handler(BodyHandler.create().setMergeFormAttributes(true));
        router.getDelegate().post("/callback").handler(callbackHandler);
    }

    protected void addProtectedEndpoint(final Router router, final String url, final String clientNames) {
        addProtectedEndpoint(router, url, clientNames, null);
    }

    protected void addProtectedEndpoint(final Router router, final String url, final String clientNames, final String authName) {
        SecurityHandlerOptions options = new SecurityHandlerOptions().setClients(clientNames);
        if (authName != null) {
            options = options.setAuthorizers(authName);
        }
        final SecurityHandler securityHandler = new SecurityHandler(vertx.getDelegate(), sessionStore, config, authProvider, options);
        router.getDelegate().route(url).handler(securityHandler);
    }

    /**
     * @param router router instance
     */
    protected void enableCorsSupport(Router router) {
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PATCH));
    }

    /**
     * @param router router instance
     */
    protected void enableLocalSession(Router router) {
        vertxSessionStore = LocalSessionStore.create(vertx);
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(vertxSessionStore));
    }

    /**
     * @param router router instance
     */
    protected void enableClusteredSession(Router router) {
        vertxSessionStore = ClusteredSessionStore.create(vertx);
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(vertxSessionStore));
    }

}
