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
        if (transportModes == null) {
            return true;
        }
        if (stopPlace.getTransportMode() == null) {
            return false;
        }
        return transportModes.stream()
                .anyMatch(mode -> mode.value().equals(stopPlace.getTransportMode().value()));
    }
}
