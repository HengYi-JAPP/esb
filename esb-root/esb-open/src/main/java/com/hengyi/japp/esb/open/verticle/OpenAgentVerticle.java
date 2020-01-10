package com.hengyi.japp.esb.open.verticle;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.open.application.TalentService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.apache.commons.io.FilenameUtils;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.vertx.auth.Pac4jAuthProvider;
import org.pac4j.vertx.handler.impl.CallbackHandler;
import org.pac4j.vertx.handler.impl.CallbackHandlerOptions;
import org.pac4j.vertx.handler.impl.SecurityHandler;
import org.pac4j.vertx.handler.impl.SecurityHandlerOptions;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.hengyi.japp.esb.open.OpenGuiceModule.getInstance;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2019-09-19
 */
public class OpenAgentVerticle extends AbstractVerticle {
    private final LocalSessionStore localSessionStore = getInstance(LocalSessionStore.class);
    private final Pac4jAuthProvider pac4jAuthProvider = getInstance(Pac4jAuthProvider.class);
    private final SessionStore pac4jSessionStore = getInstance(SessionStore.class);
    private final Config pac4jConfig = getInstance(Config.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        router.route().handler(SessionHandler.create(localSessionStore));
        router.route().handler(UserSessionHandler.create(pac4jAuthProvider));
        final CallbackHandlerOptions callbackHandlerOptions = new CallbackHandlerOptions().setMultiProfile(true);
        final CallbackHandler callbackHandler = new CallbackHandler(vertx, pac4jSessionStore, pac4jConfig, callbackHandlerOptions);
        router.get("/callback").handler(callbackHandler);
        router.post("/callback").handler(BodyHandler.create().setMergeFormAttributes(true));
        router.post("/callback").handler(callbackHandler);
        router.route("/autoLogin/*").handler(securityHandler("CasClient"));

        // 无token api
        downloads_scm_annex(router);

        router.route("/autoLogin/talent").handler(rc -> {
            final TalentService talentService = getInstance(TalentService.class);
            talentService.autoLoginUrl(rc.user()).setHandler(ar -> {
                if (ar.succeeded()) {
                    rc.response().putHeader(HttpHeaders.LOCATION, ar.result()).setStatusCode(303).end();
                } else {
                    rc.response().setStatusCode(400).end();
                }
            });
        });

        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(9999, ar -> startFuture.handle(ar.mapEmpty()));
    }

    private SecurityHandler securityHandler(String clients) {
        final SecurityHandlerOptions securityHandlerOptions = new SecurityHandlerOptions().setClients(clients);
        return new SecurityHandler(vertx, pac4jSessionStore, pac4jConfig, pac4jAuthProvider, securityHandlerOptions);
    }

    private void downloads_scm_annex(Router router) {
        router.get("/downloads/scm/annex").handler(rc -> {
            // sshfs -o ro 192.168.0.74:/app/apache-tomcat-7.0.56/webapps/scm/upload/annex /home/esb/esb-open/downloads/scm/annex
            // sshfs -o ro 192.168.0.231:/usr/local/apache-tomcat-7.0.56/webapps/scm/upload/annex /home/esb/esb-open/downloads/scm/annex
            final Path rootPath = Paths.get("/home/esb/esb-open/downloads/scm/annex");
            final Path path = Optional.ofNullable(rc.queryParam("path"))
                    .filter(J::nonEmpty)
                    .map(it -> it.get(0))
                    .map(rootPath::resolve)
                    .orElse(null);
            if (path == null || !Files.exists(path) || Files.isDirectory(path)) {
                rc.fail(500);
                return;
            }
            final String fileName = Optional.ofNullable(rc.queryParam("fileName"))
                    .filter(J::nonEmpty)
                    .map(it -> it.get(0))
                    .orElse(FilenameUtils.getName(path.toString()));
            rc.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .putHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName, UTF_8))
                    .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
                    .sendFile(path.toString()).end();
        });
    }

}