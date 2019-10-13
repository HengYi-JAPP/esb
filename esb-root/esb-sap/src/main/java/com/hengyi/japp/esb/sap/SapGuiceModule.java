package com.hengyi.japp.esb.sap;

import com.google.inject.*;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Cleanup;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import javax.inject.Named;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Properties;

import static reactor.rabbitmq.Utils.singleConnectionMono;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class SapGuiceModule extends AbstractModule {
    private static Injector SAP_INJECTOR;

    synchronized public static void init(Vertx vertx) {
        if (SAP_INJECTOR != null) {
            return;
        }
        SAP_INJECTOR = Guice.createInjector(new GuiceModule(vertx, "esb-sap"), new SapGuiceModule());
        JcoDataProvider.init();
    }

    public static <T> T getInstance(Class<T> clazz) {
        return SAP_INJECTOR.getInstance(clazz);
    }

    public static <T> T getInstance(Key<T> key) {
        return SAP_INJECTOR.getInstance(key);
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("sap.properties")
    private Properties SapProperties(@Named("rootPath") Path rootPath) {
        final File file = rootPath.resolve("sap.properties").toFile();
        @Cleanup final FileReader reader = new FileReader(file);
        final Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    @Provides
    @Singleton
    private Sender Sender(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("rabbit", new JsonObject());
        final String host = config.getString("host", "192.168.0.38");
        final String username = config.getString("username", "admin");
        final String password = config.getString("password", "tomking");
        final String clientProvidedName = config.getString("clientProvidedName", "esb-sap-sender");

        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        final Mono<? extends Connection> connectionMono = singleConnectionMono(() -> {
            final Address address = new Address(host);
            final Address[] addrs = {address};
            return connectionFactory.newConnection(addrs, clientProvidedName);
        });
        final ChannelPoolOptions channelPoolOptions = new ChannelPoolOptions().maxCacheSize(10);
        final SenderOptions senderOptions = new SenderOptions()
                .connectionFactory(connectionFactory)
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.elastic())
                .channelPool(ChannelPoolFactory.createChannelPool(connectionMono, channelPoolOptions));
        return RabbitFlux.createSender(senderOptions);
    }

    @Provides
    @Singleton
    private Receiver Receiver(@Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject config = vertxConfig.getJsonObject("rabbit", new JsonObject());
        final String host = config.getString("host", "192.168.0.38");
        final String username = config.getString("username", "admin");
        final String password = config.getString("password", "tomking");
        final String clientProvidedName = config.getString("clientProvidedName", "esb-sap-receiver");

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
