package devnoh.demoapp.security;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static KeyPair getKeyPairFromKeyStore() throws Exception {
        InputStream in = SecurityUtil.class.getResourceAsStream("/ssl/server.jks");

        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(in, "password".toCharArray()); // keystore password
        KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("password".toCharArray()); // key password

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("server", keyPassword);

        java.security.cert.Certificate cert = keyStore.getCertificate("server");
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        return new KeyPair(publicKey, privateKey);
    }

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = getKeyPairFromKeyStore();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("publicKey=" + publicKey.toString());
        System.out.println("privateKey=" + privateKey.toString());
    }

}
