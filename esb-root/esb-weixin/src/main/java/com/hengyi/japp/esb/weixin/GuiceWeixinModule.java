package com.hengyi.japp.esb.weixin;

import com.hengyi.japp.esb.core.GuiceModule;
import io.vertx.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceWeixinModule extends GuiceModule {

    protected GuiceWeixinModule(Vertx vertx) {
        super(vertx, "esb-weixin");
    }

//    @Override
//    protected void configure() {
//        bind(UnitOfWork.class).to(XodusUnitOfWork.class);
//        bind(SchedulerPunch.class).to(SchedulerPunchImpl.class).asEagerSingleton();
//        bind(WeixinService.class).to(WeixinServiceImpl.class);
//    }
//
//    @Provides
//    @Singleton
//    protected WorkClient WorkClient() {
//        Properties p = Util.readProperties(rootPath(), "weixin_work.properties");
//        return WorkClient.getInstance(p);
//    }
//
//    @Provides
//    @Singleton
//    @Named("hengyi_proDS")
//    protected JDBCClient hengyi_pro() {
//        JsonObject hycxDS = Util.readJsonObject(rootPath(), "hengyi_proDS.config.json");
//        return JDBCClient.createShared(vertx, hycxDS, "hycxDS");
//    }

//    @Provides
//    @Singleton
//    PersistentEntityStore PersistentEntityStore() {
//        return PersistentEntityStores.newInstance(FileUtils.getFile(rootPath(), "xodus"));
//    }
}
