package com.hengyi.japp.esb.oa.application.intenal;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.hengyi.japp.esb.oa.application.OaRestService;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.core.Util.apmError;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-09-27
 */
@Slf4j
@Singleton
public class OaRestServiceImpl implements OaRestService {
    @Override
    public void handler(RoutingContext rc) {
        final HttpServerRequest request = rc.request();
        final HttpMethod httpMethod = request.method();
        final Named restServiceConfigNamed = Names.named("restServiceConfig");
        final Key<JsonObject> restServiceConfigKey = Key.get(JsonObject.class, restServiceConfigNamed);
        final JsonObject restServiceConfig = OA_INJECTOR.getInstance(restServiceConfigKey);
        final String absoluteURI = restServiceConfig.getString("baseUrl") + request.uri().substring(4);
        final VertxHttpHeaders headers = new VertxHttpHeaders();
        final JsonObject headersJsonObject = restServiceConfig.getJsonObject("headers", new JsonObject());
        headersJsonObject.forEach(entry -> headers.add(entry.getKey(), entry.getValue()));
        final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
        final Span span = tracer.buildSpan(request.uri())
                .withTag(Tags.HTTP_METHOD, request.rawMethod())
                .withTag(Tags.HTTP_URL, absoluteURI)
                .withTag(Tags.COMPONENT, this.getClass().getName())
                .start();
        WebClient.create(rc.vertx())
                .request(httpMethod, absoluteURI)
                .putHeaders(headers)
                .sendBuffer(rc.getBody(), ar -> {
                    final AsyncResult<Buffer> bufferAr = ar.map(HttpResponse::body);
                    if (bufferAr.succeeded()) {
                        if (span != null) {
                            span.setTag(Tags.HTTP_STATUS, 200);
                            span.setTag(Tags.ERROR, false);
                            span.finish();
                        }
                        rc.response().end(bufferAr.result());
                    } else {
                        log.error("", bufferAr.cause());
                        apmError(rc, span, bufferAr.cause());
                        rc.fail(bufferAr.cause());
                    }
                });
    }
}
