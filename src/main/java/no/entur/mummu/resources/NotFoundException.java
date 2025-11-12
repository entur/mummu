package no.entur.mummu.resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("The requested resource was not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
