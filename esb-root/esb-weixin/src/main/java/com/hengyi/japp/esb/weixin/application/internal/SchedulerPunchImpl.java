package com.hengyi.japp.esb.weixin.application.internal;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.infrastructure.persistence.UnitOfWork;
import com.hengyi.japp.esb.weixin.application.SchedulerPunch;
import com.hengyi.japp.esb.weixin.domain.SchedulerPunchLog;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.J;
import org.jzb.weixin.work.AgentClient;
import org.jzb.weixin.work.WorkClient;
import org.jzb.weixin.work.oa.CheckinData;
import org.jzb.weixin.work.oa.GetCheckinDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.hengyi.japp.esb.weixin.MainVerticle.GUICE;

/**
 * @author jzb 2018-04-29
 */
public class SchedulerPunchImpl implements SchedulerPunch {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Scheduler scheduler;
    private final Disposable schedulerDisposable;
    private final AgentClient agentClient;
    private final JDBCClient punchDS;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final String DELETE_SQL_TPL = "delete from IF_IN_CARD_RECODE where RECODE_DATE>='${startDate}' and RECODE_DATE<'${endDate}' and COME_FROM='WEIXIN_ORIGINAL'";
    private final String INSERT_SQL_TPL = "insert into IF_IN_CARD_RECODE (ID_CODE,RECODE_DATE,RECODE_TIME,COME_FROM) values ('${ID_CODE}','${RECODE_DATE}','${RECODE_TIME}','WEIXIN_ORIGINAL')";

    @Inject
    private SchedulerPunchImpl(WorkClient workClient, Scheduler scheduler, @Named("rootPath") String rootPath, @Named("hengyi_proDS") JDBCClient punchDS) {
        this.scheduler = scheduler;
        this.punchDS = punchDS;
        final Properties p3010011 = Util.readProperties(rootPath, "weixin_agent_3010011.properties");
        this.agentClient = workClient.agentClient(p3010011);
        schedulerDisposable = schedulerDisposable();
    }

    private Disposable schedulerDisposable() {
        final long daySeconds = TimeUnit.DAYS.toSeconds(1);
        final LocalTime lt0 = LocalTime.ofSecondOfDay(0);
        final long until = LocalTime.now().until(lt0, ChronoUnit.SECONDS);
        /**
         * 时间差小于0，需要加一天的延迟
         */
        final long initialDelay = until >= 0 ? until : daySeconds + until;
        return scheduler.schedulePeriodicallyDirect(this, initialDelay, daySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        final LocalDate ldEnd = LocalDate.now();
        fetchAndUpdate(ldEnd.plusDays(-1), ldEnd).subscribe();
    }

    @Override
    public Completable fetchAndUpdate(final LocalDate ldStart, final LocalDate ldEnd) {
        final SchedulerPunchLog schedulerPunchLog = logStart();
        final Stream<String> userIdStream = ForkJoinPool.commonPool().invoke(new AgentUserIdTask(agentClient));
        return Flowable.fromIterable(userIdStream::iterator)
                .buffer(100)
                .flatMap(it -> {
                    final GetCheckinDataResponse res = agentClient.getCheckinData()
                            .addUser(it)
                            .starttime(ldStart)
                            .endtime(ldEnd)
                            .call();
                    if (!res.isSuccessed()) {
                        throw new RuntimeException();
                    }
                    final Stream<CheckinData> stream = res.checkindata();
                    return Flowable.fromIterable(stream::iterator);
                })
                .filter(CheckinData::hasCheckin)
                .map(it -> {
                    final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                    builder.put("ID_CODE", it.userid());
                    final LocalDateTime ldt = J.localDateTime(it.checkin_time());
                    builder.put("RECODE_DATE", ldt.format(dateFormat));
                    builder.put("RECODE_TIME", ldt.format(timeFormat));
                    final ImmutableMap<String, String> parmas = builder.build();
                    return J.strTpl(INSERT_SQL_TPL, parmas);
                })
                .toList()
                .flatMap(insertSqls -> punchDS.rxGetConnection().flatMap(conn -> {
                    final ImmutableMap<String, String> map = ImmutableMap.of(
                            "startDate", ldStart.format(dateFormat),
                            "endDate", ldEnd.format(dateFormat)
                    );
                    final String deleteSql = J.strTpl(DELETE_SQL_TPL, map);
                    return conn.rxExecute(deleteSql)
                            .andThen(conn.rxBatch(insertSqls))
                            .doOnSuccess(it -> log.debug("===insertSqls[" + it.size() + "]==="));
                }))
                .toCompletable()
                .doOnComplete(() -> this.logEnd(schedulerPunchLog))
                .doOnError(ex -> this.logError(schedulerPunchLog, ex));
    }

    private SchedulerPunchLog logStart() {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        final SchedulerPunchLog schedulerPunchLog = new SchedulerPunchLog();
        uow.registerNew(schedulerPunchLog);
        schedulerPunchLog.setStartDateTime(System.currentTimeMillis());
        schedulerPunchLog.setSuccessed(false);
        uow.commit();
        log.info("===打卡数据schedule 开始===");
        return schedulerPunchLog;
    }

    private void logError(SchedulerPunchLog schedulerPunchLog, Throwable ex) {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        uow.registerDirty(schedulerPunchLog);
        schedulerPunchLog.setEndDateTime(System.currentTimeMillis());
        schedulerPunchLog.setCallException(ex);
        uow.commit();
        log.error("===打卡数据schedule 失败===", ex);
    }

    private void logEnd(SchedulerPunchLog schedulerPunchLog) {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        uow.registerDirty(schedulerPunchLog);
        schedulerPunchLog.setEndDateTime(System.currentTimeMillis());
        uow.commit();
        log.info("===打卡数据schedule 成功===");
    }

    @Override
    public void cancel() {
        Optional.ofNullable(schedulerDisposable)
                .filter(it -> !it.isDisposed())
                .ifPresent(Disposable::dispose);
    }
}
