package no.entur.mummu.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.StopPlace;

import java.util.List;

public class MultimodalFilterTest {

    @Test
    void testMultiModalParentReturnsTrueWithParentFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.parent);
        Assertions.assertTrue(filter.test(createParentStop()));
    }

    @Test
    void testMultiModalChildReturnsFalseWithParentFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.parent);
        Assertions.assertFalse(filter.test(createChildStop()));
    }

    @Test
    void testMultiModalParentReturnsFalseWithChildFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.child);
        Assertions.assertFalse(filter.test(createParentStop()));
    }

    @Test
    void testMultiModalChildReturnsTrueWithChildFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.child);
        Assertions.assertTrue(filter.test(createChildStop()));
    }

    @Test
    void testMultiModalParentReturnsTrueWithBothFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.both);
        Assertions.assertTrue(filter.test(createParentStop()));
    }

    @Test
    void testMultiModalChildReturnsTrueWithBothFilterType() {
        var filter = new MultimodalFilter(MultimodalFilter.MultimodalFilterType.both);
        Assertions.assertTrue(filter.test(createChildStop()));
    }

    private StopPlace createParentStop() {
        return new StopPlace()
                .withKeyList(
                new KeyListStructure()
                        .withKeyValue(
                                List.of(
                                        new KeyValueStructure()
                                                .withKey("IS_PARENT_STOP_PLACE")
                                                .withValue("true")
                                )
                        )
                );

    }

    private StopPlace createChildStop() {
        return new StopPlace()
                .withKeyList(
                        new KeyListStructure()
                        .withKeyValue(
                                List.of(
                                        new KeyValueStructure()
                                        .withKey("IS_PARENT_STOP_PLACE")
                                        .withValue("false")
                                )
                        )
                );
    }

}
