package devnoh.demoapp.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PartnerCertificateNotFoundException extends RuntimeException {
    public PartnerCertificateNotFoundException(Long partnerId) {
        super(String.format("Partner certificate with partner id '%d' not found.", partnerId));
    }
}
