package com.hengyi.japp.esb.sap.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.sap.application.internal.NnfpServiceImpl;
import io.vertx.core.Future;

/**
 * 描述： 金税发票接口
 *
 * @author jzb 2018-03-13
 */
@ImplementedBy(NnfpServiceImpl.class)
public interface NnfpService {
    /**
     * 开票请求接口
     *
     * @throws Exception
     */
    Future<String> kpOrderSync(String order);

    /**
     * 开票结果查询接口
     *
     * @throws Exception
     */
    Future<String> queryElectricKp(String order);
}
