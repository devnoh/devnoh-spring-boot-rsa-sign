package devnoh.demoapp.repository;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
//@TestPropertySource(locations = {"classpath:application-test.properties"})
@Slf4j
public class PartnerCertificateRepositoryTest {

    static final String PEM_CERT_BEGIN = "-----BEGIN CERTIFICATE-----";
    static final String PEM_CERT_END = "-----END CERTIFICATE-----";

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    PartnerCertificateRepository partnerCertificateRepository;

    byte[] certBytes;

    X509Certificate certificate;

    @Before
    public void setUp() throws Exception {
        InputStream certIn = getClass().getResourceAsStream("/security/partner.crt");
        String certPem = IOUtils.toString(certIn, "UTF-8");
        certPem = SecurityUtil.stripCertificateBeginEndTags(certPem);
        certBytes = Base64.getDecoder().decode(certPem);
        //certIn = getClass().getResourceAsStream("/security/partner.cer");
        //certBytes = IOUtils.toByteArray(certIn);

        certificate = SecurityUtil.loadCertificate(certBytes);
        log.info("certificate={}", certificate);

        /*
        log.info("version={}", certificate.getVersion());
        log.info("serialNumber={}", certificate.getSerialNumber());
        log.info("subject={}", certificate.getSubjectDN());
        log.info("issuer={}", certificate.getIssuerDN());
        log.info("validFrom={}", certificate.getNotBefore());
        log.info("validUntil={}", certificate.getNotAfter());
        log.info("sigAlgorithm={}", certificate.getSigAlgName());
        PublicKey publicKey = certificate.getPublicKey();
        log.info("publicKey={}", publicKey.toString());
        String publicKeyAsBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        log.info("publicKeyAsBase64={}", publicKeyAsBase64);
        */
    }

    @Test
    public void testPartnerCertificate() {
        Partner partner = new Partner();
        partner.setName("Partner1");
        entityManager.persist(partner);
        log.debug("partner.saved={}", partner);

        assertNotNull(partner.getId());
        assertEquals("Partner1", partner.getName());
        Long partnerId = partner.getId();

        PartnerCertificate partnerCertificate = new PartnerCertificate();
        partnerCertificate.setPartner(partner);
        partnerCertificate.setCertificate(certBytes);
        partnerCertificate.setVersion(String.valueOf(certificate.getVersion()));
        partnerCertificate.setSerialNumber(certificate.getSerialNumber().toString());
        partnerCertificate.setSubject(certificate.getSubjectDN().toString());
        partnerCertificate.setIssuer(certificate.getIssuerDN().toString());
        partnerCertificate.setValidFrom(certificate.getNotBefore());
        partnerCertificate.setValidUntil(certificate.getNotAfter());
        partnerCertificate.setPublicKey(Base64.getEncoder().encodeToString(certificate.getPublicKey().getEncoded()));
        partnerCertificate.setSigAlgorithm(certificate.getSigAlgName());

        partnerCertificateRepository.save(partnerCertificate);
        log.debug("partnerCertificate.saved={}", partnerCertificate);
        assertNotNull(partnerCertificate.getId());

        List<PartnerCertificate> certificates =
                partnerCertificateRepository.findByPartnerIdAndValidDate(partnerId, new PageRequest(0, 1));
        assertEquals(1, certificates.size());

        PartnerCertificate certificateFound = certificates.get(0);
        log.info("partnerCertificate.found={}", certificateFound);
        assertEquals(partnerCertificate, certificateFound);
        assertNotNull(certificateFound.getCertificate());
        assertNotNull(certificateFound.getVersion());
        assertNotNull(certificateFound.getSerialNumber());
        assertNotNull(certificateFound.getSubject());
        assertNotNull(certificateFound.getIssuer());
        assertNotNull(certificateFound.getValidFrom());
        assertNotNull(certificateFound.getValidUntil());
        assertNotNull(certificateFound.getPublicKey());
        assertNotNull(certificateFound.getSigAlgorithm());
    }

}
