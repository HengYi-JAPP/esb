package com.hengyi.japp.esb.open;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.open.application.SignService;
import com.hengyi.japp.esb.open.application.TalentService;
import com.hengyi.japp.esb.open.application.TexbeedataService;
import com.hengyi.japp.esb.open.application.internal.SignServiceImpl;
import com.hengyi.japp.esb.open.application.internal.TalentServiceImpl;
import com.hengyi.japp.esb.open.application.internal.TexbeedataServiceImpl;
import com.hengyi.japp.esb.open.config.Pac4jConfigurationFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.pac4j.core.config.ConfigFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceOpenModule extends GuiceModule {

    GuiceOpenModule(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void configure() {
        bind(SignService.class).to(SignServiceImpl.class);
        bind(TalentService.class).to(TalentServiceImpl.class);
        bind(TexbeedataService.class).to(TexbeedataServiceImpl.class);
        bind(ConfigFactory.class).to(Pac4jConfigurationFactory.class);
    }

    @Provides
    @Singleton
    protected PrivateKey PrivateKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return (PrivateKey) keystore().getKey("esb-open", "esb-open-tomking".toCharArray());
    }

    private KeyStore keystore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        final KeyStore keystore = KeyStore.getInstance("jceks");
        final File file = Paths.get(rootPath(), "esb-open.jceks").toFile();
        keystore.load(new FileInputStream(file), "esb-open-tomking".toCharArray());
        return keystore;
    }

    @Provides
    @Singleton
    protected PublicKey PublicKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        return keystore().getCertificate("esb-open").getPublicKey();
    }

    @Provides
    @Singleton
    @Named("authConfig")
    protected JsonObject authConfig() {
        return vertx.getOrCreateContext().config().getJsonObject("auth");
    }

}
