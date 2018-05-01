package com.hengyi.japp.esb.sms.application.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hengyi.japp.esb.core.Util;
import io.reactivex.Flowable;
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

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.FSS_TS_OR_TX;
import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.SS_TS_OR_TX;

/**
 * 描述： 18:18资金短信发送
 * 银行托收或贴现在途的银票金额
 *
 * @author jzb 2018-03-23
 */
public class SchedulerSend1818_tsOrTxTask extends RecursiveTask<Single<Map<String, BigDecimal>>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Collection<String> ss = ImmutableSet.of("2993");
    private final Collection<String> fss = ImmutableSet.of("2994");
    private final String key;
    private final JDBCClient fiDS;

    public SchedulerSend1818_tsOrTxTask(String key, JDBCClient fiDS) {
        this.key = key;
        this.fiDS = fiDS;
    }

    @Override
    protected Single<Map<String, BigDecimal>> compute() {
        log.debug("===tsOrTx[" + key + "]===");
        switch (key) {
            case SS_TS_OR_TX:
                return Flowable.mergeArray(ts(ss), tx(ss))
                        .reduce(BigDecimal::add)
                        .toSingle(BigDecimal.ZERO)
                        .map(it -> ImmutableMap.of(key, it));
            case FSS_TS_OR_TX:
                return Flowable.mergeArray(ts(fss), tx(fss))
                        .reduce(BigDecimal::add)
                        .toSingle(BigDecimal.ZERO)
                        .map(it -> ImmutableMap.of(key, it));
            default:
                throw new RuntimeException(key + "不存在");
        }
    }

    /**
     * 贴现在途
     *
     * @param parentAgencyIds
     * @return
     */
    private Flowable<BigDecimal> tx(Collection<String> parentAgencyIds) {
        final String sqlTpl = "SELECT DISTINCT tk.BILLCODE,en.REV_AGENCY_ID,s.AGENCYNAME,tk.ID,tk.BILLSTATUS,tk.AMOUNT,s.PARENTAGENCYID,t.CURRENCYCODE  FROM TK_R_ACCEPTANCEBILL tk,tk_r_acceptancebillenter en,tk_r_acceptancebillout ou,SYS_AGENCY s,TS_ACCOUNT t  WHERE tk.ID=ou.ACCEPTANCEBILL_ID  AND s.id=t.ID_BELONGORG AND ou.ACCEPTANCEBILL_ID=en.ACCEPTANCEBILL_ID AND en.REV_AGENCY_ID=s.id  AND ou.R_VOUCHERDATE IS NULL  AND tk.BILLSTATUS=4  AND tk.BILLDESCRIPTION=1  AND t.CURRENCYCODE in ('CNY') AND s.PARENTAGENCYID IN (${parentAgencyIds})";
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("parentAgencyIds", Util.sqlIn(parentAgencyIds)));
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .map(it -> it.getDouble("AMOUNT"))
                .map(BigDecimal::valueOf);
    }

    /**
     * 托收在途
     *
     * @param parentAgencyIds
     * @return
     */
    private Flowable<BigDecimal> ts(Collection<String> parentAgencyIds) {
        final String sqlTpl = "SELECT DISTINCT tk.BILLCODE,en.REV_AGENCY_ID,s.AGENCYNAME,tk.ID,tk.BILLSTATUS,tk.AMOUNT,s.PARENTAGENCYID,t.CURRENCYCODE  from TK_R_ACCEPTANCEBILL tk,tk_r_acceptancebillenter en,tk_r_acceptancebillout ou,SYS_AGENCY s,TS_ACCOUNT t  WHERE tk.ID=ou.ACCEPTANCEBILL_ID and s.id=t.ID_BELONGORG and ou.ACCEPTANCEBILL_ID=en.ACCEPTANCEBILL_ID and en.REV_AGENCY_ID=s.id  AND ou.R_VOUCHERDATE IS NULL  and tk.BILLSTATUS=3  and tk.BILLDESCRIPTION=1  and t.CURRENCYCODE in ('CNY') and s.PARENTAGENCYID IN (${parentAgencyIds})";
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("parentAgencyIds", Util.sqlIn(parentAgencyIds)));
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .map(it -> it.getDouble("AMOUNT"))
                .map(BigDecimal::valueOf);
    }
}
