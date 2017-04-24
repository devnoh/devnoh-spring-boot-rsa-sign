package devnoh.demoapp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@Slf4j
public class SecurityUtilTest {

    static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

    static final String PEM_PUBLIC_START = "-----BEGIN PUBLIC KEY-----";
    static final String PEM_PUBLIC_END = "-----END PUBLIC KEY-----";

    PrivateKey privateKey;
    PublicKey publicKey;

    @Before
    public void setUp() throws Exception {
        // private key
        InputStream privateIn = getClass().getResourceAsStream("/security/partner_private.key");
        String privateKeyPem = IOUtils.toString(privateIn, "UTF-8");
        privateKeyPem = privateKeyPem.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
        privateKeyPem = privateKeyPem.replaceAll("\\s", "");

        DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));
        DerValue[] seq = derReader.getSequence(0);
        if (seq.length < 9) {
            throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
        }

        // skip version seq[0];
        BigInteger modulus = seq[1].getBigInteger();
        BigInteger publicExp = seq[2].getBigInteger();
        BigInteger privateExp = seq[3].getBigInteger();
        BigInteger prime1 = seq[4].getBigInteger();
        BigInteger prime2 = seq[5].getBigInteger();
        BigInteger exp1 = seq[6].getBigInteger();
        BigInteger exp2 = seq[7].getBigInteger();
        BigInteger crtCoef = seq[8].getBigInteger();

        RSAPrivateCrtKeySpec keySpec =
                new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        // public key
        InputStream publicIn = getClass().getResourceAsStream("/security/partner_public.key");
        String publicKeyPem = IOUtils.toString(publicIn, "UTF-8");
        publicKeyPem = publicKeyPem.replace(PEM_PUBLIC_START, "").replace(PEM_PUBLIC_END, "");
        publicKeyPem = publicKeyPem.replaceAll("\\s", "");

        byte[] publicBytes = Base64.getDecoder().decode(publicKeyPem);
        publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String text = "foobar";

        String encrypted = SecurityUtil.encrypt(text, publicKey);
        log.debug("encrypted={}", encrypted);

        String decrypted = SecurityUtil.decrypt(encrypted, privateKey);
        log.debug("decrypted={}", decrypted);

        assertEquals(text, decrypted);
    }

    @Test
    public void testSignVerify() throws Exception {
        String text = "foobar";

        String sign = SecurityUtil.sign(text, privateKey);
        log.debug("sign={}", sign);

        boolean verified = SecurityUtil.verify(text, sign, publicKey);
        log.debug("verified={}", verified);

        assertTrue(verified);
    }

}
