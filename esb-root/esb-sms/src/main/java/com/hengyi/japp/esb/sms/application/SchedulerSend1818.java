package com.hengyi.japp.esb.sms.application;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import org.jzb.J;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hengyi.japp.esb.sms.MainVerticle.GUICE;
import static org.jzb.Constant.MAPPER;

/**
 * 描述： 18:18资金短信发送
 *
 * @author jzb 2018-03-22
 */
public interface SchedulerSend1818 extends Runnable {
    String DATE_STRING = "dateString";
    String SS_CNY = "ssCny";
    String SS_USD = "ssUsd";
    String SS_YP = "ssYp";
    String SS_TS_OR_TX = "ssTsOrTx";
    String FSS_TS_OR_TX = "fssTsOrTx";
    String SS_GNZ = "ssGnz";
    String FSS_CNY = "fssCny";
    String FSS_USD = "fssUsd";
    String FSS_YP = "fssYp";
    String FSS_GNZ = "fssGnz";
    String NOTE = "note";
    String CONTENT_TPL = "${dateString}《恒逸资金资讯》：" +
            "1、石化股份资金池余额：" +
            "人民币${ssCny}，美元${ssUsd}，持有银票${ssYp}，银行托收或贴现在途的银票${ssTsOrTx}，国内证已承兑未融资${ssGnz}。" +
            "2、集团公司资金池余额：" +
            "人民币${fssCny}，美元${fssUsd}，持有银票${fssYp}，银行托收或贴现在途的银票${fssTsOrTx}，国内证已承兑未融资${fssGnz}。" +
            "${note}";

    static Map<String, String> convertAmount(Map<String, BigDecimal> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        it -> {
                            final BigDecimal amount = it.getValue();
                            double res = amount == null ? 0 : amount.doubleValue() / 10000;
                            return MessageFormat.format("{0}万元", (int) res);
                        })
                );
    }

    static String content(ImmutableMap.Builder<String, String> builder) {
        return J.strTpl(CONTENT_TPL, builder.build());
    }

    static Collection<String> getPhones() throws IOException {
        final Key<String> key = Key.get(String.class, Names.named("rootPath"));
        final String rootPath = GUICE.getInstance(key);
        final Path path = Paths.get(rootPath, "send1818.phones.json");
        try (final InputStream is = Files.newInputStream(path)) {
            return MAPPER.readValue(is, Set.class);
        }
    }

    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/schedulerSend1818").blockingHandler(rc -> {
            final HttpServerResponse response = rc.response();
            final JsonObject body = rc.getBodyAsJson();
            final LocalDate ld = Optional.ofNullable(body.getString("date"))
                    .filter(J::nonBlank)
                    .map(LocalDate::parse)
                    .orElse(LocalDate.now());
            send(ld).subscribe(
                    () -> response.end("ok"),
                    ex -> response.setStatusCode(400).end(ex.getMessage())
            );
        });
        return router;
    }

    Completable send(LocalDate ld);

    void cancel();

}
