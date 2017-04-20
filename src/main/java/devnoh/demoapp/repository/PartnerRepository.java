package devnoh.demoapp.repository;

import devnoh.demoapp.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

}
