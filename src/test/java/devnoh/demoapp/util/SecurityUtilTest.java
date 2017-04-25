package devnoh.demoapp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@Slf4j
public class SecurityUtilTest {

    PrivateKey privateKey;
    PublicKey publicKey;

    @Before
    public void setUp() throws Exception {
        // private key
        InputStream privateIn = getClass().getResourceAsStream("/security/partner_private.key");
        String privateKeyPem = IOUtils.toString(privateIn, "UTF-8");
        privateKey = SecurityUtil.loadPrivateKey(privateKeyPem);

        // public key
        InputStream publicIn = getClass().getResourceAsStream("/security/partner_public.key");
        String publicKeyPem = IOUtils.toString(publicIn, "UTF-8");
        publicKey = SecurityUtil.loadPublickey(publicKeyPem);
    }

    @Test
    public void testLoadCertificate() throws Exception {
        InputStream certIn = getClass().getResourceAsStream("/security/partner.crt");
        String certPem = IOUtils.toString(certIn, "UTF-8");
        X509Certificate certificate = SecurityUtil.loadCertificate(certPem);

        log.info("version={}", certificate.getVersion());
        log.info("serialNumber={}", certificate.getSerialNumber());
        log.info("subject={}", certificate.getSubjectDN());
        log.info("issuer={}", certificate.getIssuerDN());
        log.info("validFrom={}", certificate.getNotBefore());
        log.info("validUntil={}", certificate.getNotAfter());
        log.info("sigAlgorithm={}", certificate.getSigAlgName());
        PublicKey publicKey = certificate.getPublicKey();
        log.info("publicKey={}", publicKey.toString());
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String text = "foobar";

        String encrypted = SecurityUtil.encrypt(text, publicKey);
        log.info("encrypted={}", encrypted);

        String decrypted = SecurityUtil.decrypt(encrypted, privateKey);
        log.info("decrypted={}", decrypted);

        assertEquals(text, decrypted);
    }

    @Test
    public void testSignVerify() throws Exception {
        String text = "foobar";

        String sign = SecurityUtil.sign(text, privateKey);
        log.info("sign={}", sign);

        boolean verified = SecurityUtil.verify(text, sign, publicKey);
        log.info("verified={}", verified);

        assertTrue(verified);
    }
}
