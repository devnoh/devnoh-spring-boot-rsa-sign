package devnoh.demoapp.repository;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.domain.PartnerCertificate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
//@TestPropertySource(locations = {"classpath:application-test.properties"})
@Slf4j
public class PartnerCertificateRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    PartnerCertificateRepository partnerCertificateRepository;

    byte[] certBytes;

    X509Certificate certificate;

    @Before
    public void setUp() throws Exception {
        InputStream in = getClass().getResourceAsStream("/security/partner.cer");
        certBytes = IOUtils.toByteArray(in);
        log.debug("certBytes.length={}", certBytes.length);

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));

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
    }

    @Test
    public void testPartnerCertificate() {
        Partner partner = new Partner();
        partner.setName("Partner1");
        entityManager.persist(partner);
        log.debug("partner.saved={}", partner);

        assertNotNull(partner.getId());
        assertEquals("Partner1", partner.getName());
        Long parterId = partner.getId();

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

        PartnerCertificate certificateFound = partnerCertificateRepository.findOneByPartnerIdAndValidDate(parterId);
        log.debug("partnerCertificate.found={}", certificateFound);
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
