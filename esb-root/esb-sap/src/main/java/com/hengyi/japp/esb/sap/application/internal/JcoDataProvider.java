package com.hengyi.japp.esb.sap.application.internal;

import com.sap.conn.jco.ext.*;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Properties;

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

    @SneakyThrows
    synchronized public static void init(String dir) {
        if (Environment.isDestinationDataProviderRegistered()) {
            return;
        }
        final File file = FileUtils.getFile(dir, "sap.properties");
        @Cleanup final FileReader reader = new FileReader(file);
        final Properties properties = new Properties();
        properties.load(reader);
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
