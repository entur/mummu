package no.entur.mummu.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs requests whose {@code count} parameter exceeds the configured maximum, with
 * the consumer's client name, so API users relying on very large pages can be
 * notified before a max-count limit is enforced. Log-only; it does not reject or
 * clamp the request.
 */
@Component
public class MaxCountLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MaxCountLoggingFilter.class);

    private final long maxCount;

    public MaxCountLoggingFilter(@Value("${no.entur.mummu.list.max-count:1000}") long maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logIfCountExceeded(request);
        chain.doFilter(request, response);
    }

    private void logIfCountExceeded(HttpServletRequest request) {
        String countParam = request.getParameter("count");
        if (countParam == null) {
            return;
        }
        long count;
        try {
            count = Long.parseLong(countParam.trim());
        } catch (NumberFormatException e) {
            return; // not a number; leave it to normal request handling
        }
        if (count > maxCount) {
            String method = request.getMethod();
            String path = LogSanitizer.sanitize(request.getRequestURI());
            String client = LogSanitizer.sanitize(request.getHeader("ET-Client-Name"));
            log.warn("Request count {} exceeds max {}: {} {} client={}", count, maxCount, method, path, client);
        }
    }
}
