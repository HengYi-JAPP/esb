package com.hengyi.japp.esb.weixin;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.infrastructure.persistence.UnitOfWork;
import com.hengyi.japp.esb.core.infrastructure.persistence.xodus.XodusUnitOfWork;
import com.hengyi.japp.esb.weixin.application.SchedulerPunch;
import com.hengyi.japp.esb.weixin.application.WeixinService;
import com.hengyi.japp.esb.weixin.application.internal.SchedulerPunchImpl;
import com.hengyi.japp.esb.weixin.application.internal.WeixinServiceImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.jzb.weixin.work.WorkClient;

import java.util.Properties;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceWeixinModule extends GuiceModule {

    protected GuiceWeixinModule(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void configure() {
        bind(UnitOfWork.class).to(XodusUnitOfWork.class);
        bind(SchedulerPunch.class).to(SchedulerPunchImpl.class).asEagerSingleton();
        bind(WeixinService.class).to(WeixinServiceImpl.class);
    }

    @Provides
    @Singleton
    protected WorkClient WorkClient() {
        Properties p = Util.readProperties(rootPath(), "weixin_work.properties");
        return WorkClient.getInstance(p);
    }

    @Provides
    @Singleton
    @Named("hengyi_proDS")
    protected JDBCClient hengyi_pro() {
        JsonObject hycxDS = Util.readJsonObject(rootPath(), "hengyi_proDS.config.json");
        return JDBCClient.createShared(vertx, hycxDS, "hycxDS");
    }

//    @Provides
//    @Singleton
//    PersistentEntityStore PersistentEntityStore() {
//        return PersistentEntityStores.newInstance(FileUtils.getFile(rootPath(), "xodus"));
//    }
}
