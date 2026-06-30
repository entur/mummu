package no.entur.mummu.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LargeResponseLoggingFilterTest {

    @Test
    void sanitizeNeutralizesControlCharactersToPreventLogForging() {
        assertEquals("evil__INJECTED", LargeResponseLoggingFilter.sanitize("evil\r\nINJECTED"));
        assertEquals("a_b", LargeResponseLoggingFilter.sanitize("a\tb"));
        assertEquals("normal/path?ids=NSR:Quay:1", LargeResponseLoggingFilter.sanitize("normal/path?ids=NSR:Quay:1"));
        assertNull(LargeResponseLoggingFilter.sanitize(null));
    }
}
