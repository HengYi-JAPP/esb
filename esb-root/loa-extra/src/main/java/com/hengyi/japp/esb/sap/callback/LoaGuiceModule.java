package com.hengyi.japp.esb.sap.callback;

import com.google.inject.*;
import com.hengyi.japp.esb.core.GuiceModule;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import loa.biz.LOAApp;
import loa.biz.LOATextResponseChecker;
import lombok.SneakyThrows;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;

import javax.inject.Named;
import java.lang.reflect.Field;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class LoaGuiceModule extends AbstractModule {
    private static Injector LOA_INJECTOR;

    synchronized public static void init(Vertx vertx) {
        if (LOA_INJECTOR == null) {
            LOA_INJECTOR = Guice.createInjector(new GuiceModule(vertx, "loa-extra"), new LoaGuiceModule());
        }
    }

    public static <T> T getInstance(Class<T> clazz) {
        return LOA_INJECTOR.getInstance(clazz);
    }

    public static <T> T getInstance(Key<T> key) {
        return LOA_INJECTOR.getInstance(key);
    }

    @SneakyThrows
    @Provides
    @Singleton
    private LOAApp LOAApp(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("loa", new JsonObject());
        final String appUrl = config.getString("appUrl");
        final String appName = config.getString("appName");
        final String appKey = config.getString("appKey");
        final String account = config.getString("account");
        final String pwd = config.getString("pwd");
        final LOAApp app = LOAApp.getInstance();
        app.init(appUrl, appName, appKey, true);
        app.login(account, pwd);

        // fixme 临时解决自动登入
        final Field field = LOATextResponseChecker.class.getDeclaredField("_Code_ServerTimeOut");
        if (field != null) {
            field.setAccessible(true);
            field.setInt(null, 345);
        }

        return app;
    }

    @Provides
    @Singleton
    private Receiver Receiver(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("rabbit", new JsonObject());
        final String host = config.getString("host");
        final String username = config.getString("username");
        final String password = config.getString("password");
        final String clientProvidedName = config.getString("clientProvidedName", "loa-extra-receiver");

        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        final ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(connectionFactory)
                .connectionSupplier(cf -> {
                    final Address address = new Address(host);
                    return cf.newConnection(new Address[]{address}, clientProvidedName);
                });
        return RabbitFlux.createReceiver(receiverOptions);
    }

}
