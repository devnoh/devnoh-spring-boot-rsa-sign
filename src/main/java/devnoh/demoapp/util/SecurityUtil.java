package devnoh.demoapp.util;

import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SecurityUtil {

    private static final String PEM_CERT_BEGIN = "-----BEGIN CERTIFICATE-----";
    private static final String PEM_CERT_END = "-----END CERTIFICATE-----";
    private static final String PEM_PRIVATE_BEGIN = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
    private static final String PEM_PUBLIC_BEGIN = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_PUBLIC_END = "-----END PUBLIC KEY-----";

    private SecurityUtil() {
    }

    public static String stripCertificateBeginEndTags(String pem) {
        return pem.replace(PEM_CERT_BEGIN, "").replace(PEM_CERT_END, "").replaceAll("\\s", "");
    }

    public static String stripPrivateKeyBeginEndTags(String pem) {
        return pem.replace(PEM_PRIVATE_BEGIN, "").replace(PEM_PRIVATE_END, "")
                .replace(PEM_RSA_PRIVATE_BEGIN, "").replace(PEM_RSA_PRIVATE_END, "").replaceAll("\\s", "");
    }

    public static String stripPublicKeyBeginEndTags(String pem) {
        return pem.replace(PEM_PUBLIC_BEGIN, "").replace(PEM_PUBLIC_END, "").replaceAll("\\s", "");
    }

    public static X509Certificate loadCertificate(byte[] certDer) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certDer));
    }

    public static X509Certificate loadCertificate(String certPem) throws CertificateException {
        byte[] certBytes = Base64.getDecoder().decode(stripCertificateBeginEndTags(certPem));
        return loadCertificate(certBytes);
    }

    public static PrivateKey loadPrivateKey(String keyPem) throws GeneralSecurityException, IOException {
        if (keyPem.indexOf(PEM_PRIVATE_BEGIN) != -1) { // PKCS#8 format
            byte[] privateBytes = Base64.getDecoder().decode(stripPrivateKeyBeginEndTags(keyPem));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } else { // if (keyPem.indexOf(PEM_RSA_PRIVATE_BEGIN) != -1) {  // PKCS#1 format
            byte[] privateBytes = Base64.getDecoder().decode(stripPrivateKeyBeginEndTags(keyPem));
            DerInputStream derReader = new DerInputStream(privateBytes);
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
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    public static PublicKey loadPublickey(String keyPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.getDecoder().decode(stripPublicKeyBeginEndTags(keyPem));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    public static String encrypt(String plainText, PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(decriptCipher.doFinal(bytes), UTF_8);
    }

    public static String sign(String plainText, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }

    public static String getFingerprint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        byte[] digest = MessageDigest.getInstance("SHA1").digest(cert.getEncoded());
        //return DatatypeConverter.printHexBinary(digest);
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            if (builder.length() > 0) {
                builder.append(":");
            }
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

}
