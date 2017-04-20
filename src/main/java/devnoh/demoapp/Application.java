package devnoh.demoapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
    @Bean
    CommandLineRunner init(PartnerRepository partnerRepository, PartnerCertificateRepository certificateRepository) {
        return (String... args) -> {
            InputStream in = Application.class.getResourceAsStream("/ssl/partner.cer");
            byte[] derCert = IOUtils.toByteArray(in);
            log.debug("derCert.length={}", derCert.length);

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate =
                    (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(derCert));

            log.debug("version={}", certificate.getVersion());
            log.debug("serialNumber={}", certificate.getSerialNumber());
            log.debug("subject={}", certificate.getSubjectDN());
            log.debug("issuer={}", certificate.getIssuerDN());
            log.debug("validFrom={}", certificate.getNotBefore());
            log.debug("validUntil={}", certificate.getNotAfter());
            log.debug("sigAlgorithm={}", certificate.getSigAlgName());
            PublicKey publicKey = certificate.getPublicKey();
            log.debug("publicKey={}", publicKey.toString());
            String publicKeyAsBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            log.debug("publicKeyAsBase64={}", publicKeyAsBase64);

            PartnerCertificate partnerCertificate = certificateRepository.findOneByPartnerIdAndValidDate(1L);
            log.debug("partnerCertificate.found={}", partnerCertificate);

            if (partnerCertificate == null) {
                Partner partner = partnerRepository.findOne(1L);
                log.debug("partner.found={}", partner);
                partnerCertificate = new PartnerCertificate();
                partnerCertificate.setPartner(partner);
            }
            partnerCertificate.setCertificate(derCert);
            partnerCertificate.setVersion(String.valueOf(certificate.getVersion()));
            partnerCertificate.setSerialNumber(certificate.getSerialNumber().toString());
            partnerCertificate.setSubject(certificate.getSubjectDN().toString());
            partnerCertificate.setIssuer(certificate.getIssuerDN().toString());
            partnerCertificate.setValidFrom(certificate.getNotBefore());
            partnerCertificate.setValidUntil(certificate.getNotAfter());
            partnerCertificate.setPublicKey(publicKeyAsBase64);
            partnerCertificate.setSigAlgorithm(certificate.getSigAlgName());

            certificateRepository.save(partnerCertificate);
            log.debug("partnerCertificate.saved={}", partnerCertificate);
        };
    }
    */
}
