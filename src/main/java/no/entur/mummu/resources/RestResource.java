package no.entur.mummu.resources;

import no.entur.mummu.util.NetexEntityComparator;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RestResource {
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public RestResource(NetexEntitiesIndex netexEntitiesIndex) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    @GetMapping(value = "/groups-of-stop-places", produces = "application/json")
    public List<GroupOfStopPlaces> getGroupOfStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfStopPlacesIndex().getAll().stream()
                .filter(groupOfStopPlaces -> ids == null || ids.contains(groupOfStopPlaces.getId()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/groups-of-stop-places/{id}", produces = "application/json")
    public GroupOfStopPlaces getGroupOfStopPlacesById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getGroupOfStopPlacesIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/stop-places", produces = "application/json")
    public Collection<StopPlace> getStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<VehicleModeEnumeration> transportModes
    ) {
        return netexEntitiesIndex.getStopPlaceIndex().getAllVersions().keySet().stream()
                .filter(key -> ids == null || ids.contains(key))
                .map(key -> netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(key))
                .filter(stopPlace -> transportModes == null || transportModes.contains(stopPlace.getTransportMode()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value="/stop-places/{id}/parkings", produces = "application/json")
    public Collection<Parking> getParkingByStopPlaceId(@PathVariable String id) {
        if (netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id) == null) {
            throw new NotFoundException();
        }
        return netexEntitiesIndex.getParkingsByParentSiteRefIndex().get(id);
    }

    @GetMapping(value = "/quays", produces = "application/json")
    public List<Quay> getQuays(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getQuayIndex().getAllVersions().keySet().stream()
                .filter(key -> ids == null || ids.contains(key))
                .map(key -> netexEntitiesIndex.getQuayIndex().getLatestVersion(key))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/quays/{id}", produces = "application/json")
    public Quay getQuayById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "quays/{id}/stop-place", produces = "application/json")
    public StopPlace getStopPlaceByQuayId(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIdByQuayIdIndex().get(id)
        ).map(
                stopPlaceId -> netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlaceId)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/parkings", produces = "application/json")
    public List<Parking> getParkings(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getParkingIndex().getAll().stream()
                .filter(parking -> ids == null || ids.contains(parking.getId()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/parkings/{id}", produces = "application/json")
    public Parking getParkingById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/topographic-places", produces ="application/json")
    public List<TopographicPlace> getTopographicPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getTopographicPlaceIndex().getAll().stream()
                .filter(topographicPlace -> ids == null || ids.contains(topographicPlace.getId()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/topographic-places/{id}", produces = "application/json")
    public TopographicPlace getTopographicPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/tariff-zones", produces = "application/json")
    public List<TariffZone> getTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getTariffZoneIndex().getAll().stream()
                .filter(tariffZone -> ids == null || ids.contains(tariffZone.getId()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones", produces = "application/json")
    public List<FareZone> getFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getFareZoneIndex().getAll().stream()
                .filter(fareZone -> ids == null || ids.contains(fareZone.getId()))
                .sorted(new NetexEntityComparator())
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }
}
