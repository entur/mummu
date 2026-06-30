package no.entur.mummu.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs read responses whose uncompressed serialized size exceeds a threshold,
 * with the request path and consumer headers, to identify the source of the
 * large responses that trigger heap OOMs. Read-only; does not buffer the body.
 */
@Component
public class LargeResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LargeResponseLoggingFilter.class);

    private final long thresholdBytes;

    public LargeResponseLoggingFilter(
            @Value("${no.entur.mummu.logging.large-response-threshold-bytes:10485760}") long thresholdBytes) {
        this.thresholdBytes = thresholdBytes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // User-controlled values are neutralized before logging to prevent log
        // forging (CWE-117): the URI, query string and request headers can carry
        // control characters that would otherwise inject forged log lines.
        String method = request.getMethod();
        String path = sanitize(request.getRequestURI()) + query(request);
        String client = sanitize(request.getHeader("ET-Client-Name"));
        String clientId = sanitize(request.getHeader("ET-Client-ID"));
        String correlationId = sanitize(request.getHeader("X-Correlation-Id"));

        CountingResponseWrapper wrapper = new CountingResponseWrapper(response, thresholdBytes,
                () -> log.warn("Large response crossing {} bytes while streaming: {} {} client={} clientId={} correlationId={}",
                        thresholdBytes, method, path, client, clientId, correlationId));

        try {
            chain.doFilter(request, wrapper);
        } finally {
            long bytes = wrapper.bytesWritten();
            if (bytes > thresholdBytes) {
                int status = response.getStatus();
                String contentType = sanitize(response.getContentType());
                log.warn("Large response completed: {} bytes {} {} status={} contentType={} client={} clientId={} correlationId={}",
                        bytes, method, path, status, contentType, client, clientId, correlationId);
            }
        }
    }

    private static String query(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? "" : "?" + sanitize(queryString);
    }

    /**
     * Removes line breaks and other control characters from user-controlled values
     * to prevent log forging (CWE-117). The explicit CR/LF handling lets CodeQL
     * recognize this as a log-injection sanitizer (java/log-injection).
     */
    static String sanitize(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sanitized = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '\r' || ch == '\n' || Character.isISOControl(ch)) {
                sanitized.append('_');
            } else {
                sanitized.append(ch);
            }
        }
        return sanitized.toString();
    }

    private static final class CountingResponseWrapper extends HttpServletResponseWrapper {
        private final long threshold;
        private final Runnable onCrossed;
        private CountingServletOutputStream stream;

        CountingResponseWrapper(HttpServletResponse response, long threshold, Runnable onCrossed) {
            super(response);
            this.threshold = threshold;
            this.onCrossed = onCrossed;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (stream == null) {
                stream = new CountingServletOutputStream(super.getOutputStream(), threshold, onCrossed);
            }
            return stream;
        }

        long bytesWritten() {
            return stream == null ? 0 : stream.count();
        }
    }
}
