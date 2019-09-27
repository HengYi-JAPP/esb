package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfSubCompanyBean;
import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfUserBean;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class HrmServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                vertx.eventBus().consumer("esb:oa:HrmService:getHrmSubcompanyInfo", reply -> Single.fromCallable(() -> {
                    final HrmServicePortType hrmServicePortType = OA_INJECTOR.getInstance(HrmServicePortType.class);
                    final ArrayOfSubCompanyBean arrayOfSubCompanyBean = hrmServicePortType.getHrmSubcompanyInfo("");
                    return MAPPER.writeValueAsString(arrayOfSubCompanyBean.getSubCompanyBean());
                }).subscribe(it -> {
                    reply.reply(it);
                }, err -> {
                    log.error("", err);
                    reply.fail(400, err.getLocalizedMessage());
                })).rxCompletionHandler(),

                vertx.eventBus().consumer("esb:oa:HrmService:getHrmUserInfo", reply -> Single.fromCallable(() -> {
                    final HrmServicePortType hrmServicePortType = OA_INJECTOR.getInstance(HrmServicePortType.class);
                    final ArrayOfUserBean arrayOfUserBean = hrmServicePortType.getHrmUserInfo(null, null, null, null, null, null);
                    return MAPPER.writeValueAsString(arrayOfUserBean.getUserBean());
                }).subscribe(it -> {
                    reply.reply(it);
                }, err -> {
                    log.error("", err);
                    reply.fail(400, err.getLocalizedMessage());
                })).rxCompletionHandler()
        );
    }
}
