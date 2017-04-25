package devnoh.demoapp.service;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.repository.PartnerCertificateRepository;
import devnoh.demoapp.repository.PartnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
public class PartnerCertificateServiceTest {

    static final String PEM_CERT = "-----BEGIN CERTIFICATE-----\n"
            + "MIIDgzCCAmugAwIBAgIEeXutYDANBgkqhkiG9w0BAQsFADByMQswCQYDVQQGEwJV\n"
            + "UzETMBEGA1UECBMKQ2FsaWZvcm5pYTEPMA0GA1UEBxMGSXJ2aW5lMRQwEgYDVQQK\n"
            + "EwtQYXJ0bmVyIEluYzEVMBMGA1UECxMMUGFydG5lciBVbml0MRAwDgYDVQQDEwdQ\n"
            + "YXJ0bmVyMB4XDTE3MDQxOTA3MTU0MFoXDTI3MDQxNzA3MTU0MFowcjELMAkGA1UE\n"
            + "BhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExDzANBgNVBAcTBklydmluZTEUMBIG\n"
            + "A1UEChMLUGFydG5lciBJbmMxFTATBgNVBAsTDFBhcnRuZXIgVW5pdDEQMA4GA1UE\n"
            + "AxMHUGFydG5lcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAINxuGFA\n"
            + "/jFJ6F0GzulRzuCi8Ob4t6aSOhJpELQrVTh2NtZvK1w1tYcb1lB/mYv/nojHxICV\n"
            + "6SPJMuCzZUAQQBGsdaT0etQJkpxB64jHrRhBYqUEOFz+9DTBm8CA95Gz1fQ57DwZ\n"
            + "TjbKKalwxHFb03bUYWNBoPeXwfsmfmeKY1BChL1jF2NRwP5IEs0A98bBpgymS+7n\n"
            + "qjEV/wo7CeTY+hqFN6uBhcB+nIKgfFac/XGQzsnoYlySgVT2y/PxU96r5B71yOL0\n"
            + "YPzR+f16EFt1JvJsonjy32sbXNekDAwDzexMb0/WcqkNBTlQqDu9c6fO7JGybec3\n"
            + "Nn8EuN51rUkLNN0CAwEAAaMhMB8wHQYDVR0OBBYEFCkK9mh7CSwM6Tl6OdRrjpx8\n"
            + "jg+aMA0GCSqGSIb3DQEBCwUAA4IBAQBS2Nim+8bI6u1fdz9HjXxhaWIETHQi8kws\n"
            + "uY6CmDi5EBebLdLu12+smwvbP9DQSdXT2XUNt4UwRRc5/RDuXeTBfagyRUWTBrWZ\n"
            + "AIg/cKDs9NIRRS8Iu6fjSc20QYv1Vsv5zcFI4k6ERF6obflzR1rxtXaP/qKoe8I/\n"
            + "PWzQbMlI3FCanvU+PJtT41l6OiTRKkLdnV3vbDpcPQKKwAs4ZHdMPMWVgrdK/XZt\n"
            + "d0w6zpt86kU7LK28Xy5eE1HlnIbAeef/1O9gs2HDj0kMRS/bn1hYPwukGi9EUdbf\n"
            + "LaH+D48QqQcyrGRFurY+wXWQaSeAMUBQlRLP28hJZHQTNebRANuM\n"
            + "-----END CERTIFICATE-----\n";

    @InjectMocks
    PartnerCertificateService partnerCertificateService;

    @Mock
    PartnerRepository partnerRepository;

    @Mock
    PartnerCertificateRepository partnerCertificateRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePartnerCertificate() throws Exception {

        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("Partner 1");

        when(partnerRepository.findOne(1L)).thenReturn(partner);

        PartnerCertificate partnerCertificate = partnerCertificateService.createPartnerCertificate(1L, PEM_CERT);
        log.info("partnerCertificate={}", partnerCertificate);

        assertNotNull(partnerCertificate.getCertificate());
        assertNotNull(partnerCertificate.getVersion());
        assertNotNull(partnerCertificate.getSerialNumber());
        assertNotNull(partnerCertificate.getSubject());
        assertNotNull(partnerCertificate.getIssuer());
        assertNotNull(partnerCertificate.getValidFrom());
        assertNotNull(partnerCertificate.getValidUntil());
        assertNotNull(partnerCertificate.getPublicKey());
        assertNotNull(partnerCertificate.getSigAlgorithm());
    }

}
