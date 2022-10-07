package no.entur.mummu.updater;

import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

import java.util.Collection;
import java.util.Map;

public class StopPlaceUpdate {
    private Map<String, Collection<StopPlace>> versions;
    private Map<String, Collection<Quay>> quayVersions;
    private Map<String, Collection<Parking>> parkingVersions;

    public Map<String, Collection<StopPlace>> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, Collection<StopPlace>> versions) {
        this.versions = versions;
    }

    public Map<String, Collection<Quay>> getQuayVersions() {
        return quayVersions;
    }

    public void setQuayVersions(Map<String, Collection<Quay>> quayVersions) {
        this.quayVersions = quayVersions;
    }

    public Map<String, Collection<Parking>> getParkingVersions() {
        return parkingVersions;
    }

    public void setParkingVersions(Map<String, Collection<Parking>> parkingVersions) {
        this.parkingVersions = parkingVersions;
    }
}
