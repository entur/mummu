package no.entur.mummu.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.time.LocalDateTime;

class ModifiedSinceFilterTest {

    private static final LocalDateTime REFERENCE_TIME = LocalDateTime.of(2024, 6, 15, 12, 0, 0);

    @Test
    void testReturnsTrue_WhenModifiedSinceIsNull() {
        var filter = new ModifiedSinceFilter(null);
        var stopPlace = new StopPlace().withChanged(REFERENCE_TIME);

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenEntityChangedAfterModifiedSince() {
        var filter = new ModifiedSinceFilter(REFERENCE_TIME);
        var stopPlace = new StopPlace().withChanged(REFERENCE_TIME.plusDays(1));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenEntityChangedBeforeModifiedSince() {
        var filter = new ModifiedSinceFilter(REFERENCE_TIME);
        var stopPlace = new StopPlace().withChanged(REFERENCE_TIME.minusDays(1));

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenEntityChangedExactlyAtModifiedSince() {
        var filter = new ModifiedSinceFilter(REFERENCE_TIME);
        var stopPlace = new StopPlace().withChanged(REFERENCE_TIME);

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenEntityChangedIsNull() {
        var filter = new ModifiedSinceFilter(REFERENCE_TIME);
        var stopPlace = new StopPlace();

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenModifiedSinceIsNullAndEntityChangedIsNull() {
        var filter = new ModifiedSinceFilter(null);
        var stopPlace = new StopPlace();

        Assertions.assertTrue(filter.test(stopPlace));
    }
}
