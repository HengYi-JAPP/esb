package com.hengyi.japp.esb.sms.application.internal;

import com.google.common.collect.ImmutableMap;
import com.hengyi.japp.esb.sms.application.SchedulerSend1818;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static com.hengyi.japp.esb.sms.application.SchedulerSend1818.*;

/**
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818_contentTask extends RecursiveTask<Single<String>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final LocalDate ld;
    private final JDBCClient fiDS;
    private final JDBCClient hycxDS;

    SchedulerSend1818_contentTask(LocalDate ld, JDBCClient fiDS, JDBCClient hycxDS) {
        this.ld = ld;
        this.fiDS = fiDS;
        this.hycxDS = hycxDS;
    }

    @Override
    protected Single<String> compute() {
        log.debug("===content===");
        final ForkJoinTask<Single<Map<String, String>>> noteTask = new SchedulerSend1818_noteTask(hycxDS, ld).fork();
        return Flowable.mergeArray(
                Flowable.fromArray(
                        new SchedulerSend1818_ypTask(SS_YP, fiDS).fork(),
                        new SchedulerSend1818_ypTask(FSS_YP, fiDS).fork(),
                        new SchedulerSend1818_tsOrTxTask(SS_TS_OR_TX, fiDS).fork(),
                        new SchedulerSend1818_tsOrTxTask(FSS_TS_OR_TX, fiDS).fork(),
                        new SchedulerSend1818_gnzTask(fiDS, ld).fork()
                ),
                balanceMap().flatMapPublisher(it -> Flowable.fromArray(
                        new SchedulerSend1818_xjTask(SS_CNY, it, fiDS).fork(),
                        new SchedulerSend1818_xjTask(FSS_CNY, it, fiDS).fork(),
                        new SchedulerSend1818_xjTask(SS_USD, it, fiDS).fork(),
                        new SchedulerSend1818_xjTask(FSS_USD, it, fiDS).fork()
                ))
        ).flatMap(Flowable::fromFuture)
                .flatMapSingle(it -> it.map(SchedulerSend1818::convertAmount))
                .mergeWith(noteTask.join().toFlowable())
                .toList()
                .map(it -> {
                    final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                    builder.put(DATE_STRING, getDateString());
                    it.forEach(builder::putAll);
                    return builder;
                })
                .map(SchedulerSend1818::content);
    }

    private String getDateString() {
        return ld.getYear() + "年" + ld.getMonthValue() + "月" + ld.getDayOfMonth() + "日";
    }

    /**
     * 取所有账号余额
     *
     * @return
     */
    private Single<Map<Long, BigDecimal>> balanceMap() {
        final String sqlTpl = "SELECT c.ACCOUNTID,c.BALANCEDATE,c.BALANCE FROM BP_ACCTCURBALANCE c WHERE to_char(c.BALANCEDATE,'yyyy-mm-dd')='${date}' UNION SELECT h.ACCOUNTID,h.BALANCEDATE,h.BALANCE FROM BP_ACCTHISBALANCE h WHERE to_char(h.BALANCEDATE,'yyyy-mm-dd')='${date}'";
        final String sql = J.strTpl(sqlTpl, ImmutableMap.of("date", ld.toString()));
        return fiDS.rxCall(sql)
                .flattenAsFlowable(ResultSet::getRows)
                .toMap(it -> it.getLong("ACCOUNTID"), it -> {
                    final Double balance = it.getDouble("BALANCE");
                    return BigDecimal.valueOf(balance);
                });
    }

}
