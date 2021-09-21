package no.entur.mummu.util;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;

import java.util.List;
import java.util.function.Predicate;

public class StopPlaceTypesFilter implements Predicate<StopPlace> {
    private final List<StopTypeEnumeration> stopTypes;

    public StopPlaceTypesFilter(List<StopTypeEnumeration> stopStypes) {
        this.stopTypes = stopStypes;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        return stopTypes == null || stopTypes.contains(stopPlace.getStopPlaceType());
    }
}
