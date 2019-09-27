package com.hengyi.japp.esb.sap;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.Cleanup;
import lombok.SneakyThrows;

import javax.inject.Named;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class SapGuiceModule extends AbstractModule {

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

}
