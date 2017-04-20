package devnoh.demoapp.repository;

import devnoh.demoapp.domain.PartnerCertificate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartnerCertificateRepository extends JpaRepository<PartnerCertificate, Long> {

    @Query("select a from PartnerCertificate a "
            + "where a.partner.id = ?1 and a.validFrom <= CURRENT_TIMESTAMP and a.validUntil >= CURRENT_TIMESTAMP")
    List<PartnerCertificate> findByPartnerIdAndValidDate(Long partnerId, Pageable pageable);



    default PartnerCertificate findOneByPartnerIdAndValidDate(Long partnerId) {
        List<PartnerCertificate> list =
                findByPartnerIdAndValidDate(partnerId, new PageRequest(0, 1, Sort.Direction.DESC, "id"));
        return list.isEmpty() ? null : list.get(0);
    }

}
