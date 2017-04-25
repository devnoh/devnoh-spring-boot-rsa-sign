package devnoh.demoapp.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SecurityServiceException extends RuntimeException {

    public SecurityServiceException() {
        super();
    }

    public SecurityServiceException(String message) {
        super(message);
    }

    public SecurityServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityServiceException(Throwable cause) {
        super(cause);
    }
}
