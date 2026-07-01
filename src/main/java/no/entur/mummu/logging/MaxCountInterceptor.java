package no.entur.mummu.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Guards the {@code count} parameter on list endpoints against a per-endpoint maximum.
 * Over-limit requests are always logged (with the consumer's client name); when
 * {@code no.entur.mummu.list.enforce-max-count} is true they are additionally rejected
 * with a 400. Enforcement is off by default so this can be shipped ahead of notifying
 * affected API users.
 */
@Component
public class MaxCountInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MaxCountInterceptor.class);

    private final MaxCountProperties properties;

    public MaxCountInterceptor(MaxCountProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // When ids are supplied the effective limit is the number of ids, not count.
        if (request.getParameter("ids") != null) {
            return true;
        }
        String countParam = request.getParameter("count");
        if (countParam == null) {
            return true;
        }
        long count;
        try {
            count = Long.parseLong(countParam.trim());
        } catch (NumberFormatException e) {
            return true; // not a number; leave it to normal request handling
        }

        String endpoint = firstPathSegment(request.getRequestURI());
        long max = properties.maxFor(endpoint);
        if (count <= max) {
            return true;
        }

        String safeEndpoint = LogSanitizer.sanitize(endpoint);
        String client = LogSanitizer.sanitize(request.getHeader("ET-Client-Name"));
        log.warn("Request count {} exceeds max {} for /{}: client={}", count, max, safeEndpoint, client);

        if (properties.isEnforceMaxCount()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "count " + count + " exceeds the maximum " + max + " for /" + safeEndpoint
                            + "; page with skip/count or use the bulk NeTEx export");
            return false;
        }
        return true;
    }

    private static String firstPathSegment(String uri) {
        int start = uri.startsWith("/") ? 1 : 0;
        int slash = uri.indexOf('/', start);
        return slash < 0 ? uri.substring(start) : uri.substring(start, slash);
    }
}
