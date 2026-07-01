package no.entur.mummu.resources;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RestErrorControllerTest {

    private final RestErrorController controller = new RestErrorController();

    @Test
    void includesMessageForClientErrors() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 400);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "count 10 exceeds the maximum 5");

        var body = controller.error(request).getBody();

        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("count 10 exceeds the maximum 5", body.get("message"));
    }

    @Test
    void omitsMessageForServerErrors() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "internal detail");

        var body = controller.error(request).getBody();

        assertFalse(body.containsKey("message"), "5xx must not leak internal messages");
    }
}
