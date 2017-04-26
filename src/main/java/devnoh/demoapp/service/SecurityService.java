package devnoh.demoapp.service;

import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.dto.RequestMessage;
import devnoh.demoapp.dto.ResponseMessage;
import devnoh.demoapp.dto.Security;
import devnoh.demoapp.error.PartnerCertificateNotFoundException;
import devnoh.demoapp.error.SecurityServiceException;
import devnoh.demoapp.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SecurityService {

    @Value(value = "${demoapp.keystore.file-path}")
    private String keystoreFilePath;

    @Value(value = "${demoapp.keystore.password}")
    private String keystorePassword;

    @Value(value = "${demoapp.keystore.key-password}")
    private String keystoreKeyPassword;

    @Value(value = "${demoapp.keystore.key-alias}")
    private String keystoreKeyAlias;

    @Autowired
    private PartnerCertificateService partnerCertificateService;

    public void addMessageSignature(RequestMessage message) throws SecurityServiceException {
        message.setSecurity(new Security(generateMessageSignature(message.getPayload().toString())));
    }

    public void addMessageSignature(ResponseMessage message) throws SecurityServiceException {
        message.setSecurity(new Security(generateMessageSignature(message.getPayload().toString())));
    }

    private KeyPair getKeyPairFromServerKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException {
        InputStream in = SecurityUtil.class.getResourceAsStream(keystoreFilePath);
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(in, keystorePassword.toCharArray()); // keystore password
        KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(keystoreKeyPassword.toCharArray());
        KeyStore.PrivateKeyEntry privateKeyEntry =
                (KeyStore.PrivateKeyEntry) keyStore.getEntry(keystoreKeyAlias, protection);
        Certificate cert = keyStore.getCertificate(keystoreKeyAlias);
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();
        return new KeyPair(publicKey, privateKey);
    }

    private String generateMessageSignature(String payload) {
        try {
            PrivateKey privateKey = getKeyPairFromServerKeyStore().getPrivate();
            return SecurityUtil.sign(payload, privateKey);
        } catch (Exception e) {
            throw new SecurityServiceException(e.getMessage(), e);
        }
    }

    public void verifyMessageSignature(RequestMessage message, Long partnerId) throws SecurityServiceException {
        verifyMessageSignature(message.getPayload().toString(), message.getSecurity().getSig(), partnerId);
    }

    public void verifyMessageSignature(ResponseMessage message, Long partnerId) throws SecurityServiceException {
        verifyMessageSignature(message.getPayload().toString(), message.getSecurity().getSig(), partnerId);
    }

    private void verifyMessageSignature(String payload, String signature, Long partnerId)
            throws SecurityServiceException {
        try {
            PartnerCertificate partnerCertificate =
                    partnerCertificateService.getLastValidPartnerCertificate(partnerId);
            if (partnerCertificate == null) {
                throw new PartnerCertificateNotFoundException(partnerId);
            }
            if (StringUtils.isEmpty(signature)) {
                throw new RuntimeException("Signature is missing");
            }

            byte[] keyBytes = Base64.getDecoder().decode(partnerCertificate.getPublicKey());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));

            boolean verified = SecurityUtil.verify(payload, signature, publicKey);
            if (!verified) {
                throw new RuntimeException("Signature is invalid");
            }
        } catch (Exception e) {
            throw new SecurityServiceException(e.getMessage(), e);
        }
    }

}
