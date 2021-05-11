package no.entur.mummu.resources;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.servers.Server;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class RestResource {
    private final NetexEntityIndexReadOnlyView netexEntityIndex;

    @Autowired
    public RestResource(NetexEntityIndexReadOnlyView netexEntityIndex) {
        this.netexEntityIndex = netexEntityIndex;
    }

    @GetMapping(value = "/group-of-stop-places/{id}", produces = "application/json")
    public GroupOfStopPlaces getGroupOfStopPlacesById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getGroupOfStopPlacesById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getStopPlaceById().lookupLastVersionById(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/quays/{id}", produces = "application/json")
    public Quay getQuayById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getQuayById().lookupLastVersionById(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/parkings/{id}", produces = "application/json")
    public Parking getParkingById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getParkingById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/topographic-places/{id}", produces = "application/json")
    public TopographicPlace getTopographicPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getTopographicPlaceById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getTariffZonesById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntityIndex.getFareZoneById().lookup(id)
        ).orElseThrow(NotFoundException::new);
    }
}
