package no.entur.mummu.util;

import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.StopPlace;

import java.util.function.Predicate;

public class MultimodalFilter implements Predicate<StopPlace> {

    public static final String IS_PARENT_STOP_PLACE = "IS_PARENT_STOP_PLACE";

    public enum MultimodalFilterType {
        parent,
        child,
        both
    }

    private final MultimodalFilterType type;

    public MultimodalFilter(MultimodalFilterType type) {
        this.type = type;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        if (type == MultimodalFilterType.parent) {
            return isParentStopPlace(stopPlace);
        } else if (type == MultimodalFilterType.child) {
            return !isParentStopPlace(stopPlace);
        }
        return true;
    }

    private static boolean isParentStopPlace(StopPlace stopPLace) {
        return stopPLace.getKeyList().getKeyValue().stream()
                .filter(keyValueStructure -> keyValueStructure.getKey().equals(MultimodalFilter.IS_PARENT_STOP_PLACE))
                .map(KeyValueStructure::getValue)
                .map(Boolean::valueOf)
                .findFirst().orElse(false);
    }
}
