package no.entur.mummu.util;

import org.rutebanken.netex.model.StopPlace;

import java.util.function.Predicate;

public class MultimodalFilter implements Predicate<StopPlace> {
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
            return stopPlace.getKeyList().getKeyValue().stream().anyMatch(keyValueStructure ->
                    keyValueStructure.getKey().equals("IS_PARENT_STOP_PLACE") && keyValueStructure.getValue().equals("true"));
        } else if (type == MultimodalFilterType.child) {
            return stopPlace.getKeyList().getKeyValue().stream().anyMatch(keyValueStructure ->
                    keyValueStructure.getKey().equals("IS_PARENT_STOP_PLACE") && keyValueStructure.getValue().equals("false"));
        } else {
            return true;
        }
    }
}
