package devnoh.demoapp.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PartnerNotFoundException extends RuntimeException {
    public PartnerNotFoundException(Long id) {
        super(String.format("Partner with id '%d' not found.", id));
    }
}
