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

        CountingResponseWrapper wrapper = new CountingResponseWrapper(response, thresholdBytes,
                () -> log.warn("Large response crossing {} bytes while streaming: {} {}{} client={} clientId={} correlationId={}",
                        thresholdBytes, request.getMethod(), request.getRequestURI(), query(request),
                        request.getHeader("ET-Client-Name"), request.getHeader("ET-Client-ID"),
                        request.getHeader("X-Correlation-Id")));

        try {
            chain.doFilter(request, wrapper);
        } finally {
            long bytes = wrapper.bytesWritten();
            if (bytes > thresholdBytes) {
                log.warn("Large response completed: {} bytes {} {}{} status={} contentType={} client={} clientId={} correlationId={}",
                        bytes, request.getMethod(), request.getRequestURI(), query(request), response.getStatus(),
                        response.getContentType(), request.getHeader("ET-Client-Name"), request.getHeader("ET-Client-ID"),
                        request.getHeader("X-Correlation-Id"));
            }
        }
    }

    private static String query(HttpServletRequest request) {
        return request.getQueryString() == null ? "" : "?" + request.getQueryString();
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
