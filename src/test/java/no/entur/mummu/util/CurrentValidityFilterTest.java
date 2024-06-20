package no.entur.mummu.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.ValidBetween;

import java.time.LocalDate;
import java.time.ZoneId;

class CurrentValidityFilterTest {


    @Test
    void testCurrentlyValidStopIsIncluded() {
        var filter = new CurrentValidityFilter(ZoneId.of("Europe/Oslo"));

        var subject = new StopPlace()
                .withValidBetween(
                        new ValidBetween()
                                .withFromDate(LocalDate.now().atStartOfDay())
                );

        Assertions.assertTrue(filter.test(subject));
    }

    @Test
    void testCurrentlyValidStopWithFutureToDateIsIncluded() {
        var filter = new CurrentValidityFilter(ZoneId.of("Europe/Oslo"));

        var subject = new StopPlace()
                .withValidBetween(
                        new ValidBetween()
                                .withFromDate(LocalDate.now().atStartOfDay())
                                .withToDate(LocalDate.now().atStartOfDay().plusDays(7))
                );

        Assertions.assertTrue(filter.test(subject));
    }

    @Test
    void testCurrentlyExpiredStopIsNotIncluded() {
        var filter = new CurrentValidityFilter(ZoneId.of("Europe/Oslo"));
        var subject = new StopPlace()
                .withValidBetween(
                        new ValidBetween()
                                .withFromDate(LocalDate.now().atStartOfDay().minusDays(7))
                                .withToDate(LocalDate.now().atStartOfDay().minusDays(1))
                );

        Assertions.assertFalse(filter.test(subject));
    }
}
