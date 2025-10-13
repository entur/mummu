package no.entur.mummu.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.StopPlace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StopPlaceByQuayIdsFilterTest {

    @Test
    void testReturnsTrue_WhenQuayIdsListIsNull() {
        var filter = new StopPlaceByQuayIdsFilter(null);
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenQuayIdsListIsEmpty() {
        var filter = new StopPlaceByQuayIdsFilter(Collections.emptyList());
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenStopPlaceHasNoQuays() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1"));
        var stopPlace = new StopPlace();

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenStopPlaceHasMatchingQuay() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1"));
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenStopPlaceHasOneOfMultipleMatchingQuays() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1", "NSR:Quay:2"));
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1", "NSR:Quay:3"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenStopPlaceHasNoMatchingQuays() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1", "NSR:Quay:2"));
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:3", "NSR:Quay:4"));

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenFilterHasSingleQuayIdAndStopPlaceHasMultipleQuaysWithMatch() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:2"));
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1", "NSR:Quay:2", "NSR:Quay:3"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testHandlesNullQuaysInStopPlace() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1"));
        var quayIds = new ArrayList<String>();
        quayIds.add("NSR:Quay:1");
        quayIds.add(null);
        quayIds.add("NSR:Quay:2");
        var stopPlace = createStopPlaceWithQuays(quayIds);

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenOnlyNullQuaysInStopPlace() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1"));
        var quays = new ArrayList<>();
        quays.add(null);
        var stopPlace = new StopPlace()
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(quays));

        Assertions.assertFalse(filter.test(stopPlace));
    }

    @Test
    void testReturnsTrue_WhenAllQuaysMatch() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1", "NSR:Quay:2", "NSR:Quay:3"));
        var stopPlace = createStopPlaceWithQuays(List.of("NSR:Quay:1", "NSR:Quay:2", "NSR:Quay:3"));

        Assertions.assertTrue(filter.test(stopPlace));
    }

    @Test
    void testReturnsFalse_WhenStopPlaceHasEmptyQuaysList() {
        var filter = new StopPlaceByQuayIdsFilter(List.of("NSR:Quay:1"));
        var stopPlace = new StopPlace()
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(Collections.emptyList()));

        Assertions.assertFalse(filter.test(stopPlace));
    }

    private StopPlace createStopPlaceWithQuays(List<String> quayIds) {
        var quays = quayIds.stream()
                .map(id -> id != null ? new Quay().withId(id) : null)
                .map(quay -> (Object) quay)
                .toList();

        return new StopPlace()
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(quays));
    }
}
