package devnoh.demoapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.dto.RequestHeader;
import devnoh.demoapp.dto.RequestMessage;
import devnoh.demoapp.dto.ResponseHeader;
import devnoh.demoapp.dto.ResponseMessage;
import devnoh.demoapp.error.SecurityServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
public class SecurityServiceTest {

    static final String PUBLIC_KEY_BASE64 =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg3G4YUD+MUnoXQbO6VHO4KLw5vi3ppI6EmkQtCtVOHY21m8rXDW1hxvWUH+Zi/+eiMfEgJXpI8ky4LNlQBBAEax1pPR61AmSnEHriMetGEFipQQ4XP70NMGbwID3kbPV9DnsPBlONsopqXDEcVvTdtRhY0Gg95fB+yZ+Z4pjUEKEvWMXY1HA/kgSzQD3xsGmDKZL7ueqMRX/CjsJ5Nj6GoU3q4GFwH6cgqB8Vpz9cZDOyehiXJKBVPbL8/FT3qvkHvXI4vRg/NH5/XoQW3Um8myiePLfaxtc16QMDAPN7ExvT9ZyqQ0FOVCoO71zp87skbJt5zc2fwS43nWtSQs03QIDAQAB";

    static final String INVALID_SIGNATURE =
            "JgoTKqDVPEslRrlso0Onw4dA1mSbCZf1fBo/+mCZmNV+pRrjovKvYIBtLzP9fyyS2xr+xqXHWzcFh2IDCydDyP2LMCOidUKbQLChkjW3C7xCLUZ+2DWdgNx3T2vdC3oWO3Vq8oapuVzfZcpksFpDR+lYJnkBQslgf6lAEwn+DBE5+DQ67IoJQCZu519Qk4JD/x6KukdvJ1Q7/1xJfeJv64h2m0mAx70urwWEOJsh6tLGvd8BjhTuALkIlASbeQXCTopyA8kGiuDRsBCnv2ng+EiEtgxZ2IOvHq47b9h6fVZKThz4EGWvSZYKzpQMD82z3byN/s9XHwPGs7Nyr25cyA==";

    @InjectMocks
    SecurityService securityService;

    @Mock
    PartnerCertificateService partnerCertificateService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    RequestMessage requestMessage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(securityService, "keystoreFilePath", "/security/server.jks");
        ReflectionTestUtils.setField(securityService, "keystorePassword", "password");
        ReflectionTestUtils.setField(securityService, "keystoreKeyPassword", "password");
        ReflectionTestUtils.setField(securityService, "keystoreKeyAlias", "server");

        ObjectMapper mapper = new ObjectMapper();

        String requestJson = new String(
                Files.readAllBytes(Paths.get(getClass().getResource("/rest/creditapp_request.json").toURI())));
        requestMessage = mapper.readValue(requestJson, RequestMessage.class);
        log.debug("requestMessage={}", requestMessage);
    }

    @Test
    public void testAddMessageSignature() throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setHeader(new RequestHeader("1"));
        requestMessage.setPayload("eyJhcHBsaWNhdGlvbklkIjoiMzMwMDE1In0K"); // {"applicationId":"330015"}

        securityService.addMessageSignature(requestMessage);
        assertNotNull(requestMessage.getSecurity());

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setHeader(new ResponseHeader(ResponseHeader.STATUS_SUCCESS));
        responseMessage.setPayload("eyJhcHBsaWNhdGlvbklkIjoiMzMwMDE1In0K"); // {"applicationId":"330015"}

        securityService.addMessageSignature(responseMessage);
        assertNotNull(responseMessage.getSecurity());
    }

    @Test
    public void testVerifyMessageSignature() throws Exception {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("Partner 1");

        PartnerCertificate partnerCertificate = new PartnerCertificate();
        partnerCertificate.setId(1L);
        partnerCertificate.setPartner(partner);
        partnerCertificate.setPublicKey(PUBLIC_KEY_BASE64);

        when(partnerCertificateService.getLastValidPartnerCertificate(1L)).thenReturn(partnerCertificate);
        securityService.verifyMessageSignature(requestMessage, 1L);

        requestMessage.getSecurity().setSig(INVALID_SIGNATURE);
        exception.expect(SecurityServiceException.class);
        exception.expectMessage("Signature is invalid");
        securityService.verifyMessageSignature(requestMessage, 1L);
    }

}
