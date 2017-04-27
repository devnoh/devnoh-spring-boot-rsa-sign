package devnoh.demoapp.service;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.domain.PartnerCertificate;
import devnoh.demoapp.error.PartnerNotFoundException;
import devnoh.demoapp.repository.PartnerCertificateRepository;
import devnoh.demoapp.repository.PartnerRepository;
import devnoh.demoapp.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import javax.transaction.Transactional;

@Service
@Slf4j
public class PartnerCertificateService {

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerCertificateRepository partnerCertificateRepository;

    @Transactional
    public PartnerCertificate createPartnerCertificate(Long partnerId, String pemCert) throws CertificateException {

        Partner partner = partnerRepository.findOne(partnerId);
        if (partner == null) {
            throw new PartnerNotFoundException(partnerId);
        }

        X509Certificate certificate = SecurityUtil.loadCertificate(pemCert);
        log.debug("certificate={}", certificate);

        PartnerCertificate partnerCertificate = new PartnerCertificate();
        partnerCertificate.setPartner(partner);
        partnerCertificate.setCertificate(certificate.getEncoded());
        partnerCertificate.setVersion(String.valueOf(certificate.getVersion()));
        partnerCertificate.setSerialNumber(certificate.getSerialNumber().toString());
        partnerCertificate.setSubject(certificate.getSubjectDN().toString());
        partnerCertificate.setIssuer(certificate.getIssuerDN().toString());
        partnerCertificate.setValidFrom(certificate.getNotBefore());
        partnerCertificate.setValidUntil(certificate.getNotAfter());
        partnerCertificate.setPublicKey(Base64.getEncoder().encodeToString(certificate.getPublicKey().getEncoded()));
        partnerCertificate.setSigAlgorithm(certificate.getSigAlgName());

        partnerCertificateRepository.save(partnerCertificate);
        log.debug("partnerCertificate.created={}", partnerCertificate);

        return partnerCertificate;
    }

    public PartnerCertificate getLastValidPartnerCertificate(Long partnerId) {
        List<PartnerCertificate> list = partnerCertificateRepository.findByPartnerIdAndValidDate(partnerId,
                new PageRequest(0, 1, Sort.Direction.DESC, "id"));
        return list.isEmpty() ? null : list.get(0);
    }

}
