package com.hengyi.japp.esb.sap.application.internal;

import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sap.conn.jco.ext.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Properties;

import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;

/**
 * 描述： SAP JCO 连接数据配置
 *
 * @author jzb 2018-03-17
 */
@Slf4j
public class JcoDataProvider implements DestinationDataProvider, ServerDataProvider {
    public static final String KEY = "";
    private final Properties properties;

    private JcoDataProvider(final Properties properties) {
        this.properties = properties;
        Environment.registerDestinationDataProvider(this);
        Environment.registerServerDataProvider(this);
        log.info("===注册SAP DataProvider===");
        Collections.list(properties.keys())
                .stream()
                .sorted()
                .forEach(k -> System.out.println("===" + k + "=" + properties.get(k) + "==="));
    }

    synchronized public static void init() {
        if (Environment.isDestinationDataProviderRegistered()) {
            return;
        }
        final Named named = Names.named("sap.properties");
        final Key<Properties> key = Key.get(Properties.class, named);
        final Properties properties = SAP_INJECTOR.getInstance(key);
        new JcoDataProvider(properties);
    }

    @Override
    public Properties getDestinationProperties(String s) {
        return properties;
    }

    @Override
    public Properties getServerProperties(String s) {
        return properties;
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setServerDataEventListener(ServerDataEventListener serverDataEventListener) {
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
    }
}
