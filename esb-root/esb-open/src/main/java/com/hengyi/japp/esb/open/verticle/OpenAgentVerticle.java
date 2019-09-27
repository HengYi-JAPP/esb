package com.hengyi.japp.esb.open.verticle;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.core.Util;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import org.apache.commons.io.FilenameUtils;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.open.OpenVerticle.OPEN_INJECTOR;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2019-09-19
 */
public class OpenAgentVerticle extends AbstractVerticle {
    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        // 无token api
        router.get("/downloads/scm/annex").handler(rc -> {
            // sshfs -o ro 192.168.0.74:/app/apache-tomcat-7.0.56/webapps/scm/upload/annex /home/esb/esb-open/downloads/scm/annex
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
            //http://localhost:9999/downloads/scm/annex
            rc.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .putHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, UTF_8))
                    .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
                    .sendFile(path.toString()).end();
        });

        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9999)
                .ignoreElement();
    }

    private void rxSend(RoutingContext rc, String address, String message, String apmOperationName) {
        final Tracer tracer = OPEN_INJECTOR.getInstance(Tracer.class);
        final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
        final Span span = initApm(rc, tracer, this, apmOperationName, address, deliveryOptions, message);
        vertx.eventBus().<String>rxSend(address, message, deliveryOptions).subscribe(reply -> {
            apmSuccess(rc, span, reply);
            rc.response().end(reply.body());
        }, err -> {
            apmError(rc, span, err);
            rc.fail(err);
        });
    }

}