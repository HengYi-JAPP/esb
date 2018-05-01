package com.hengyi.japp.esb.sms.application.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hengyi.japp.esb.core.Util;
import io.reactivex.Single;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.*;

/**
 * 描述： 18:18资金短信发送
 * 计算现金金额
 *
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818_xjTask extends RecursiveTask<Single<Map<String, BigDecimal>>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 上市CNY:  'CNY'   '2993','3090'
     * 上市USD:  'USD'   '2993','3090'
     * 非上市CNY:  'CNY'   '2994','3094'
     * 非上市USD:  'USD'   '2994','3094'
     */
    private final Collection<String> cny = ImmutableSet.of("CNY");
    private final Collection<String> usd = ImmutableSet.of("USD");
    private final Collection<String> ss = ImmutableSet.of("2993", "3090");
    private final Collection<String> fss = ImmutableSet.of("2994", "3094");
    private final String key;
    private final Map<Long, BigDecimal> balanceMap;
    private final JDBCClient fiDS;

    SchedulerSend1818_xjTask(String key, Map<Long, BigDecimal> balanceMap, JDBCClient fiDS) {
        this.key = key;
        this.balanceMap = balanceMap;
        this.fiDS = fiDS;
    }

    @Override
    protected Single<Map<String, BigDecimal>> compute() {
        log.debug("===xj[" + key + "]===");
        switch (key) {
            case SS_CNY:
                return xj(ss, cny);
            case FSS_CNY:
                return xj(fss, cny);
            case SS_USD:
                return xj(ss, usd);
            case FSS_USD:
                return xj(fss, usd);
            default:
                throw new RuntimeException(key + "不存在");
        }
    }

    private Single<Map<String, BigDecimal>> xj(Collection<String> parentAgencyIds, Collection<String> currencyCodes) {
        final String sqlTpl = "SELECT s.AGENCYCODE,s.AGENCYNAME,s.PARENTAGENCYID,t.id,t.RECBANKNAME,t.ACCOUNTNO,t.ACCOUNTDEPOSITTYPECODE, t.FUNDPROPERTYCODE,t.ACCOUNTTYPECODE,t.CURRENCYCODE FROM hy_csh.SYS_AGENCY s,hy_csh.TS_ACCOUNT t where s.id=t.ID_BELONGORG and t.CURRENCYCODE in (${currencyCodes}) and s.PARENTAGENCYID in (${parentAgencyIds}) and t.fundpropertycode in ('1000','1001','1002','1003','1009','1011','1012','1013','1014','1034') and t.STATUS<>0 and t.ACCOUNTSTATUS<>4 and s.AGENCYCODE <> 'D000'";
        final ImmutableMap<String, String> map = ImmutableMap.of(
                "currencyCodes", Util.sqlIn(currencyCodes),
                "parentAgencyIds", Util.sqlIn(parentAgencyIds)
        );
        final String sql = J.strTpl(sqlTpl, map);
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .map(it -> it.getLong("ID"))
                .map(it -> balanceMap.getOrDefault(it, BigDecimal.ZERO))
                .reduce(BigDecimal::add)
                .toSingle(BigDecimal.ZERO)
                .map(it -> ImmutableMap.of(key, it));
    }

}
