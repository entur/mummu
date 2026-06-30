package no.entur.mummu.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogSanitizerTest {

    @Test
    void neutralizesControlCharactersToPreventLogForging() {
        assertEquals("evil__INJECTED", LogSanitizer.sanitize("evil\r\nINJECTED"));
        assertEquals("a_b", LogSanitizer.sanitize("a\tb"));
        assertEquals("some-client-name", LogSanitizer.sanitize("some-client-name"));
        assertNull(LogSanitizer.sanitize(null));
    }
}
