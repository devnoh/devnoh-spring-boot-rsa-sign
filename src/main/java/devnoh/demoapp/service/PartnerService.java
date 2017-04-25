package devnoh.demoapp.service;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    public Partner getPartner(Long partnerId) {
        return partnerRepository.findOne(partnerId);
    }

}
