package com.hengyi.japp.esb.sms.application.internal;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Single;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.NOTE;

/**
 * 描述： 18:18资金短信发送
 * 备注获取
 *
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818_noteTask extends RecursiveTask<Single<Map<String, String>>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String sqlTpl = "SELECT * FROM zj_sms_beizhu WHERE cur_date='${date}'";
    private final JDBCClient hycxDS;
    private final LocalDate ld;

    SchedulerSend1818_noteTask(JDBCClient hycxDS, LocalDate ld) {
        this.hycxDS = hycxDS;
        this.ld = ld;
    }

    @Override
    protected Single<Map<String, String>> compute() {
        log.debug("===note===");
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("date", ld.toString()));
        return hycxDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .firstElement()
                .map(it -> it.getString("BEIZHU"))
                .toSingle("")
                .map(it -> ImmutableMap.of(NOTE, it));
    }
}
