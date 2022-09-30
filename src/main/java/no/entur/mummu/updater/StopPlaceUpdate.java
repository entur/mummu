package no.entur.mummu.updater;

import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

import java.util.Collection;
import java.util.Map;

public class StopPlaceUpdate {
    private String stopPlaceId;
    private Collection<StopPlace> versions;
    private Map<String, Collection<Quay>> quayVersions;
    private Map<String, Collection<Parking>> parkingVersions;

    public String getStopPlaceId() {
        return stopPlaceId;
    }

    public void setStopPlaceId(String stopPlaceId) {
        this.stopPlaceId = stopPlaceId;
    }

    public Collection<StopPlace> getVersions() {
        return versions;
    }

    public void setVersions(Collection<StopPlace> versions) {
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
