package com.hengyi.japp.esb.open;

import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 描述： 对外签名
 * keytool -genkey -keystore esb-open.jceks -storetype jceks -storepass esb-open-tomking -keyalg RSA -keysize 2048 -alias esb-open -keypass esb-open-tomking -sigalg SHA512withRSA -dname "CN=esb-open, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-20
 */
public class SignUtil {
    public static final String PUBLIC_KEY_STRING;
    public static final PrivateKey PRIVATE_KEY;
    public static final PublicKey PUBLIC_KEY;

    static {
        try {
            final KeyStore keystore = KeyStore.getInstance("jceks");
            keystore.load(new FileInputStream(FileUtils.getFile("", "esb-open.jceks")), "esb-open-tomking".toCharArray());
            PRIVATE_KEY = (PrivateKey) keystore.getKey("esb-open", "esb-open-tomking".toCharArray());
            final Certificate cert = keystore.getCertificate("esb-open");
            PUBLIC_KEY = cert.getPublicKey();
            PUBLIC_KEY_STRING = Base64.getEncoder().encodeToString(PUBLIC_KEY.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        final String s = "test";
        // final byte[] signature = sign(s);
        // final String signOut = encoder.encodeToString(signature);
        // System.out.println("signOut=" + signOut);

        final Signature sig = Signature.getInstance("SHA512withRSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(PUBLIC_KEY_STRING));
        final PublicKey pk = kf.generatePublic(keyspec);
        sig.initVerify(pk);
        sig.update(s.getBytes(UTF_8));
        // System.out.println(sig.verify(decoder.decode(signOut)));
    }

}
