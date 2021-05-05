package org.entur.mummu.resources;

import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class RestResource {
    private final NetexEntityIndexReadOnlyView netexEntityIndex;

    @Autowired
    public RestResource(NetexEntityIndexReadOnlyView netexEntityIndex) {
        this.netexEntityIndex = netexEntityIndex;
    }

    @GetMapping("/group-of-stop-places/{id}")
    public GroupOfStopPlaces getGroupOfStopPlacesById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getGroupOfStopPlacesById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/stop-places/{id}")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getStopPlaceById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/quays/{id}")
    public Quay getQuayById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getQuayById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/parkings/{id}")
    public Parking getParkingById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getParkingById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/topographic-places/{id}")
    public TopographicPlace getTopographicPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getTopographicPlaceById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/tariff-zones/{id}")
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getTariffZonesById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/fare-zones/{id}")
    public FareZone getFareZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getFareZoneById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }
}
