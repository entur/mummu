package no.entur.mummu.util;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.util.List;
import java.util.function.Predicate;

public class TransportModesFilter implements Predicate<StopPlace> {
    private final List<VehicleModeEnumeration> transportModes;

    public TransportModesFilter(List<VehicleModeEnumeration> transportModes) {
        this.transportModes = transportModes;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        return transportModes == null || transportModes.contains(stopPlace.getTransportMode());
    }
}
