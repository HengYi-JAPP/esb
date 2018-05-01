package com.hengyi.japp.esb.sap.application.internal;

import com.sap.conn.jco.ext.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

/**
 * 描述： SAP JCO 连接数据配置
 *
 * @author jzb 2018-03-17
 */
public class JcoDataProvider implements DestinationDataProvider, ServerDataProvider {
    public static final String KEY = "";
    private static final Logger log = LoggerFactory.getLogger(JcoDataProvider.class);
    private final Properties properties;

    private JcoDataProvider(final Properties properties) {
        this.properties = properties;
        Environment.registerDestinationDataProvider(this);
        Environment.registerServerDataProvider(this);
        log.info("===注册SAP DataProvider===");
        Collections.list(properties.keys())
                .stream()
                .sorted()
                .forEach(k -> log.info("===" + k + "=" + properties.get(k) + "==="));
    }

    public static void init(Properties properties) {
        if (Environment.isDestinationDataProviderRegistered()) {
            return;
        }
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