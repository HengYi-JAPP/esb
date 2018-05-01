package com.hengyi.japp.esb.sms.application.internal;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.J;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.RecursiveTask;

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.FSS_GNZ;
import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.SS_GNZ;

/**
 * 描述： 18:18资金短信发送
 * 计算国内证已承兑未融资金额
 *
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818_gnzTask extends RecursiveTask<Single<Map<String, BigDecimal>>> {
    private final String sqlTpl = "SELECT DISTINCT z.parentagencyid as type,sum(g.PAYMENTAMOUNT) as AMOUNT  FROM( select distinct c.ISSUECOMPANYID ,s.agencyname,s.parentagencyid from  FM_V2_LCREDITCONTRACT c ,SYS_AGENCY s where s.id=c.issuecompanyid )z  left join ( select c.CONTRACTNO,c.PROVIDER,c.PROVIDERNAME,b.ISSUENO,b.CREDITLETTERTYPE,a.PRESENTATIONDATE as date1,a.PAYMENTAMOUNT,a.PAYDATE,a.ACCOUNTSTATION,b.ISSUEDATE as date2,c.ISSUECOMPANYID   from FM_V2_LCREDITSETTLELASINFO a,FM_V2_LCREDITSETTLEMENT b,FM_V2_LCREDITCONTRACT c   where a.SETTLEINFOID=b.id  and b.CONTRACTID=c.id  and a.ACCOUNTSTATION in (1,-1)  and c.CONTRACTTYPE=1 and a.state<>0  and b.STATE=1 and c.ISSUECOMPANYID='2999'  and b.ISSUEDATE is not null and a.PAYDATE is not null and b.CREDITLETTERTYPE=2  union select c.CONTRACTNO,c.PROVIDER,c.PROVIDERNAME,b.ISSUENO,b.CREDITLETTERTYPE,a.PRESENTATIONDATE as date1,a.PAYMENTAMOUNT,a.PAYDATE,a.ACCOUNTSTATION,b.ISSUEDATE as date2,c.ISSUECOMPANYID   from FM_V2_LCREDITSETTLELASINFO a,FM_V2_LCREDITSETTLEMENT b,FM_V2_LCREDITCONTRACT c   where a.SETTLEINFOID=b.id  and b.CONTRACTID=c.id  and a.ACCOUNTSTATION in (1,-1)  and c.CONTRACTTYPE=1 and a.state<>0  and b.STATE=1  and c.ISSUECOMPANYID<>'2999'   and to_char(a.PRESENTATIONDATE+7,'yyyy-mm-dd')<='${date}' and to_char(a.PAYDATE,'yyyy-MM-dd')>='${date}'  and b.CREDITLETTERTYPE=2 )g  on z.ISSUECOMPANYID=g.ISSUECOMPANYID group by z.parentagencyid";
    private final JDBCClient fiDS;
    private final LocalDate ld;
    private final Function<JsonObject, String> keyF = it -> {
        final Integer type = it.getInteger("TYPE");
        switch (type) {
            //上市
            case 2993:
                return SS_GNZ;
            //非上市
            case 2994:
                return FSS_GNZ;
            default:
                return "";
        }
    };

    SchedulerSend1818_gnzTask(JDBCClient fiDS, LocalDate ld) {
        this.fiDS = fiDS;
        this.ld = ld;
    }

    @Override
    protected Single<Map<String, BigDecimal>> compute() {
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("date", ld.toString()));
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .filter(it -> Objects.nonNull(it.getValue("AMOUNT")))
                .toMultimap(keyF, it -> it.getDouble("AMOUNT"))
                .map(it -> {
                    final double ssGnz = J.emptyIfNull(it.get(SS_GNZ))
                            .stream()
                            .mapToDouble(t -> t)
                            .sum();
                    final double fssGnz = J.emptyIfNull(it.get(FSS_GNZ))
                            .stream()
                            .mapToDouble(t -> t)
                            .sum();
                    return ImmutableMap.of(SS_GNZ, BigDecimal.valueOf(ssGnz), FSS_GNZ, BigDecimal.valueOf(fssGnz));
                });
    }

}
