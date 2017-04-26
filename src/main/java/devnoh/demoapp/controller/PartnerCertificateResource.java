package devnoh.demoapp.controller;

import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.service.PartnerCertificateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PartnerCertificateResource {

    @Autowired
    private PartnerCertificateService partnerCertificateService;

    @PostMapping(value = "/partnercertificate/{partnerId}")
    @ResponseBody
    public PartnerCertificate postPartnerCertificate(@PathVariable Long partnerId, @RequestBody String pemCert)
            throws Exception {
        try {
            log.debug("certPem={}", pemCert);
            PartnerCertificate partnerCertificate =
                    partnerCertificateService.createPartnerCertificate(partnerId, pemCert);
            log.debug("partnerCertificate={}", partnerCertificate);
            return partnerCertificate;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
