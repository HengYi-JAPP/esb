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

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.FSS_YP;
import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.SS_YP;

/**
 * 描述： 18:18资金短信发送
 * 计算持有银票金额
 *
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818_ypTask extends RecursiveTask<Single<Map<String, BigDecimal>>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Collection<String> ss = ImmutableSet.of("2993");
    private final Collection<String> fss = ImmutableSet.of("2994");
    private final String key;
    private final JDBCClient fiDS;

    SchedulerSend1818_ypTask(String key, JDBCClient fiDS) {
        this.key = key;
        this.fiDS = fiDS;
    }

    @Override
    protected Single<Map<String, BigDecimal>> compute() {
        log.debug("===xj[" + key + "]===");
        switch (key) {
            case SS_YP:
                return yp(ss);
            case FSS_YP:
                return yp(fss);
            default:
                throw new RuntimeException(key + "不存在");
        }
    }

    private Single<Map<String, BigDecimal>> yp(Collection<String> parentAgencyIds) {
        final String sqlTpl = "SELECT DISTINCT tk.BILLCODE,en.REV_AGENCY_ID,s.AGENCYNAME,tk.ID,tk.BILLSTATUS,tk.AMOUNT,s.PARENTAGENCYID,t.CURRENCYCODE  FROM TK_R_ACCEPTANCEBILL tk,tk_r_acceptancebillenter en,SYS_AGENCY s,TS_ACCOUNT t  where tk.ID=en.ACCEPTANCEBILL_ID AND s.id=t.ID_BELONGORG and en.REV_AGENCY_ID=s.id and en.R_VOUCHERDATE is null and tk.BILLSTATUS=1 and tk.BILLDESCRIPTION=1 AND t.CURRENCYCODE IN ('CNY')  and s.PARENTAGENCYID in (${parentAgencyIds})";
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("parentAgencyIds", Util.sqlIn(parentAgencyIds)));
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .map(it -> it.getDouble("AMOUNT"))
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal::add)
                .toSingle(BigDecimal.ZERO)
                .map(it -> ImmutableMap.of(key, it));
    }

}
