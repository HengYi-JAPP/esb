package com.hengyi.japp.esb.sms.application.internal;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.infrastructure.persistence.UnitOfWork;
import com.hengyi.japp.esb.sms.application.SchedulerSend1818;
import com.hengyi.japp.esb.sms.application.SmsService;
import com.hengyi.japp.esb.sms.domain.SmsSend1818Log;
import com.hengyi.japp.esb.sms.dto.SmsSendDTO;
import com.hengyi.japp.esb.sms.dto.SmsSendResponseDTO;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static com.hengyi.japp.esb.sms.MainVerticle.GUICE;

/**
 * @author jzb 2018-03-22
 */
public class SchedulerSend1818Impl implements SchedulerSend1818 {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Scheduler scheduler;
    private final Disposable schedulerDisposable;
    private final SmsService smsService;
    private final JDBCClient fiDS;
    private final JDBCClient hycxDS;

    @Inject
    SchedulerSend1818Impl(SmsService smsService, Scheduler scheduler, @Named("fiDS") JDBCClient fiDS, @Named("hycxDS") JDBCClient hycxDS) {
        this.smsService = smsService;
        this.scheduler = scheduler;
        this.fiDS = fiDS;
        this.hycxDS = hycxDS;
        schedulerDisposable = schedulerDisposable();
    }

    private Disposable schedulerDisposable() {
        final long daySeconds = TimeUnit.DAYS.toSeconds(1);
        final LocalTime lt1818 = LocalTime.of(18, 18);
        final long until = LocalTime.now().until(lt1818, ChronoUnit.SECONDS);
        /**
         * 时间差小于0，需要加一天的延迟
         */
        final long initialDelay = until >= 0 ? until : daySeconds + until;
        return scheduler.schedulePeriodicallyDirect(this, initialDelay, daySeconds, TimeUnit.SECONDS);
    }

    @Override
    public Completable send(LocalDate ld) {
        final SmsSend1818Log smsSend1818Log = logStart();
        return ForkJoinPool.commonPool().invoke(new SchedulerSend1818_contentTask(ld, fiDS, hycxDS))
                .flatMap(content -> {
                    log.debug(content);
                    final SmsSendDTO smsSendDTO = new SmsSendDTO();
                    smsSendDTO.setContent(content);
                    smsSendDTO.setPhones(SchedulerSend1818.getPhones());
                    return smsService.send(smsSendDTO);
                })
                .doOnSuccess(res -> logEnd(smsSend1818Log, res))
                .doOnError(ex -> logError(smsSend1818Log, ex))
                .toCompletable();
    }

    @Override
    public void run() {
        send(LocalDate.now()).subscribe();
    }

    private SmsSend1818Log logStart() {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        final SmsSend1818Log smsSend1818Log = new SmsSend1818Log();
        uow.registerNew(smsSend1818Log);
        smsSend1818Log.setStartDateTime(System.currentTimeMillis());
        smsSend1818Log.setSuccessed(false);
        uow.commit();
        log.info("===1818 sms schedule 开始===");
        return smsSend1818Log;
    }

    private void logEnd(SmsSend1818Log smsSend1818Log, SmsSendResponseDTO smsSendResponseDTO) {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        uow.registerDirty(smsSend1818Log);
        final boolean successed = smsSendResponseDTO.isSuccess();
        final String note = smsSendResponseDTO.getResp();
        smsSend1818Log.setEndDateTime(System.currentTimeMillis());
        smsSend1818Log.setSuccessed(successed);
        smsSend1818Log.setNote(note);
        uow.commit();
        if (successed) {
            log.info("===1818 sms schedule 成功===");
        } else {
            log.error(note);
        }
    }

    private void logError(SmsSend1818Log smsSend1818Log, Throwable ex) {
        final UnitOfWork uow = GUICE.getInstance(UnitOfWork.class);
        uow.registerDirty(smsSend1818Log);
        smsSend1818Log.setEndDateTime(System.currentTimeMillis());
        smsSend1818Log.setCallException(ex);
        uow.commit();
        log.error("===1818 sms schedule 失败===", ex);
    }

    @Override
    public void cancel() {
        Optional.ofNullable(schedulerDisposable)
                .filter(it -> !it.isDisposed())
                .ifPresent(Disposable::dispose);
    }

}
