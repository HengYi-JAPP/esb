package com.hengyi.japp.esb.sap.interfaces.nnfp;

import io.reactivex.Single;

/**
 * 描述： 金税发票接口
 *
 * @author jzb 2018-03-13
 */
public interface NnfpService {
    /**
     * 开票请求接口
     *
     * @throws Exception
     */
    Single<String> kpOrderSync(String order);

    /**
     * 开票结果查询接口
     *
     * @throws Exception
     */
    Single<String> queryElectricKp(String order);
}
