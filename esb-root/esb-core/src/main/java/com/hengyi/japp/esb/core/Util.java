package com.hengyi.japp.esb.core;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.core.apm.RCTextMapExtractAdapter;
import com.hengyi.japp.esb.core.apm.RCTextMapInjectAdapter;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;

/**
 * 描述：
 *
 * @author jzb 2018-03-19
 */
@Slf4j
public class Util {
    /**
     * 只做身份验证，无法生成签名
     *
     * @param vertx
     * @return
     */
    public static JWTAuth createJwtAuth(Vertx vertx) {
        final PubSecKeyOptions rs512 = new PubSecKeyOptions()
                .setAlgorithm("RS512")
                .setPublicKey(Constant.JWT.PUBLIC_KEY);
        final JWTAuthOptions jwtAuthOptions = new JWTAuthOptions().addPubSecKey(rs512);
        return JWTAuth.create(vertx, jwtAuthOptions);
    }

    public static <T> Mono<T> mono(Future<T> future) {
        return Mono.create(monoSink -> future.setHandler(it -> {
            if (it.succeeded()) {
                final T result = it.result();
                if (result == null) {
                    monoSink.success();
                } else {
                    monoSink.success(result);
                }
            } else {
                monoSink.error(it.cause());
            }
        }));
    }

    public static Span initApm(RoutingContext rc, Tracer tracer, Object component, String operationName, String address, DeliveryOptions deliveryOptions, String message) {
        try {
            final HttpServerRequest request = rc.request();
            final Span span = tracer.buildSpan(operationName)
                    .withTag(Tags.HTTP_METHOD, request.rawMethod())
                    .withTag(Tags.HTTP_URL, request.absoluteURI())
                    .withTag(Tags.COMPONENT, component.getClass().getName())
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_PRODUCER)
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, address)
                    .start();
            tracer.inject(span.context(), TEXT_MAP, new RCTextMapInjectAdapter(deliveryOptions));
            return span.log(message);
        } catch (Throwable e) {
            log.error("initApm", e);
            return null;
        }
    }

    public static void apmSuccess(RoutingContext rc, Span span, Message reply) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.HTTP_STATUS, 200);
        span.setTag(Tags.ERROR, false);
        span.finish();
    }

    public static void apmError(RoutingContext rc, Span span, Throwable err) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.HTTP_STATUS, 400);
        span.setTag(Tags.ERROR, true);
        span.log(err.getMessage());
        span.finish();
    }

    public static Span initApm(Message reply, Tracer tracer, Object component, String operationName, String address) {
        try {
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter(reply));
            final Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName)
                    .withTag(Tags.COMPONENT, component.getClass().getName())
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, address);
            if (spanContext != null) {
                spanBuilder.asChildOf(spanContext);
            }
            return spanBuilder.start();
        } catch (Throwable e) {
            log.error("initApm", e);
            return null;
        }
    }

    public static void apmSuccess(Span span, String message) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, false);
        if (J.nonBlank(message)) {
            span.log(message);
        }
        span.finish();
    }

    public static void apmError(Span span, Throwable err) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, true);
        span.log(err.getMessage());
        span.finish();
    }

    public static void apmSuccess(Message reply, Span span, String message) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, false);
        span.log(message);
        span.finish();
    }

    public static void apmError(Message reply, Span span, Throwable err) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, true);
        span.log(err.getMessage());
        span.finish();
    }

    @SneakyThrows
    public static Properties readProperties(String first, String... more) {
        final Path path = Paths.get(first, more);
        final Properties properties = new Properties();
        @Cleanup final FileReader reader = new FileReader(path.toFile());
        properties.load(reader);
        return properties;
    }

    public static <T> T readJson(Class<T> clazz, String json) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject readJsonObject(String first, String... more) {
        try {
            final Path path = Paths.get(first, more);
            final Map map = MAPPER.readValue(path.toFile(), Map.class);
            return new JsonObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sqlIn(Collection<String> ss) {
        return ss.stream()
                .parallel()
                .distinct()
                .map(it -> "'" + it + "'")
                .collect(Collectors.joining(","));
    }

}
