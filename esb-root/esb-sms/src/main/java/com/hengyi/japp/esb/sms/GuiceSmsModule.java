package com.hengyi.japp.esb.sms;

import cn.com.flaginfo.ws.SmsPortType;
import cn.com.flaginfo.ws.Sms_Service;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.infrastructure.persistence.UnitOfWork;
import com.hengyi.japp.esb.core.infrastructure.persistence.xodus.XodusUnitOfWork;
import com.hengyi.japp.esb.sms.application.SchedulerSend1818;
import com.hengyi.japp.esb.sms.application.SmsService;
import com.hengyi.japp.esb.sms.application.internal.SchedulerSend1818Impl;
import com.hengyi.japp.esb.sms.application.internal.SmsServiceImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import org.apache.commons.io.FileUtils;

import java.util.Properties;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceSmsModule extends GuiceModule {

    GuiceSmsModule(Vertx vertx) {
        super(vertx, module);
    }

    @Override
    protected void configure() {
        bind(SchedulerSend1818.class).to(SchedulerSend1818Impl.class).asEagerSingleton();
        bind(UnitOfWork.class).to(XodusUnitOfWork.class);
        bind(SmsService.class).to(SmsServiceImpl.class);
    }

    @Provides
    @Singleton
    @Named("smsConfig")
    Properties smsConfig() {
        return Util.readProperties(rootPath(), "sms.config.properties");
    }

    @Provides
    @Singleton
    @Named("fiDS")
    JDBCClient fiDS() {
        JsonObject fiDS = Util.readJsonObject(rootPath(), "fiDS.config.json");
        return JDBCClient.createShared(vertx, fiDS, "fiDS");
    }

    @Provides
    @Singleton
    @Named("hycxDS")
    JDBCClient hycxDS() {
        JsonObject hycxDS = Util.readJsonObject(rootPath(), "hycxDS.config.json");
        return JDBCClient.createShared(vertx, hycxDS, "hycxDS");
    }

    @Provides
    SmsPortType SmsPortType() {
        return new Sms_Service().getSmsHttpPort();
    }

    @Provides
    @Singleton
    PersistentEntityStore PersistentEntityStore() {
        return PersistentEntityStores.newInstance(FileUtils.getFile(rootPath(), "xodus"));
    }
}
