package com.hengyi.japp.esb.sms.application;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sms.dto.SmsSendDTO;
import com.hengyi.japp.esb.sms.dto.SmsSendResponseDTO;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;

/**
 * 短信发送服务
 *
 * @author jzb 2018-03-18
 */
public interface SmsService {

    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/send").blockingHandler(rc -> {
            final HttpServerResponse response = rc.response();
            final SmsSendDTO command = Util.readJson(SmsSendDTO.class, rc.getBodyAsString());
            send(command).subscribe(
                    res -> {
                        if (res.isSuccess()) {
                            response.end();
                        } else {
                            response.setStatusCode(400).end(res.getResp());
                        }
                    },
                    ex -> response.setStatusCode(500).end(ex.getMessage())
            );
        });
        return router;
    }

    /**
     * 调用一信通
     *
     * @param command
     * @return
     */
    Single<SmsSendResponseDTO> send(SmsSendDTO command);
}
