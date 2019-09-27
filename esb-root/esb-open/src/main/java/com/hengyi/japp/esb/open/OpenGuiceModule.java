package com.hengyi.japp.esb.open;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.json.JsonObject;

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
public class OpenGuiceModule extends AbstractModule {

//    @Override
//    protected void configure() {
//        bind(SignService.class).to(SignServiceImpl.class);
//        bind(TalentService.class).to(TalentServiceImpl.class);
//        bind(TexbeedataService.class).to(TexbeedataServiceImpl.class);
//    }

    @Provides
    @Singleton
    private PrivateKey PrivateKey(KeyStore keystore) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return (PrivateKey) keystore.getKey("esb-open", "esb-open-tomking".toCharArray());
    }

    @Provides
    @Singleton
    private KeyStore KeyStore(@Named("vertxConfig") JsonObject vertxConfig) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        final KeyStore keystore = KeyStore.getInstance("jceks");
        final File file = Paths.get(vertxConfig.getString("rootPath"), "esb-open.jceks").toFile();
        keystore.load(new FileInputStream(file), "esb-open-tomking".toCharArray());
        return keystore;
    }

    @Provides
    @Singleton
    private PublicKey PublicKey(KeyStore keystore) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        return keystore.getCertificate("esb-open").getPublicKey();
    }

}
