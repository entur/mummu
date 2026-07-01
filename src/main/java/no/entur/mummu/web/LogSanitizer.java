package no.entur.mummu.web;

/**
 * Neutralizes control characters in user-controlled values before they are logged,
 * to prevent log forging (CWE-117). The explicit CR/LF handling lets CodeQL
 * recognize this as a log-injection sanitizer (java/log-injection).
 */
final class LogSanitizer {

    private LogSanitizer() {
    }

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
}
