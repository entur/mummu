package no.entur.mummu.resources;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public class RestErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute(ERROR_STATUS_CODE);
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ignored) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
