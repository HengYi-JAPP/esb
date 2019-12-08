package com.hengyi.japp.esb.weixin.application;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.jzb.J;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 描述： 打卡数据
 *
 * @author jzb 2018-03-22
 */
public interface SchedulerPunch extends Runnable {

    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/schedulerPunch").blockingHandler(rc -> {
            final HttpServerResponse response = rc.response();
            final JsonObject body = rc.getBodyAsJson();
            final LocalDate ldStart = Optional.ofNullable(body.getString("startDate"))
                    .filter(J::nonBlank)
                    .map(LocalDate::parse)
                    .orElse(LocalDate.now());
            final LocalDate ldEnd = Optional.ofNullable(body.getString("endDate"))
                    .filter(J::nonBlank)
                    .map(LocalDate::parse)
                    .orElse(LocalDate.now());
            fetchAndUpdate(ldStart, ldEnd).subscribe(
                    it -> response.end("ok"),
                    ex -> response.setStatusCode(400).end(ex.getMessage())
            );
        });
        return router;
    }

    void fetchAndUpdate(LocalDate startDate, LocalDate endDate);

    void cancel();
}
