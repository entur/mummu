package no.entur.mummu.resources;

import no.entur.mummu.util.NetexIdComparator;
import no.entur.mummu.util.NetexIdFilter;
import no.entur.mummu.util.NetexTechnicalIdComparator;
import no.entur.mummu.util.StopPlaceTypesFilter;
import no.entur.mummu.util.TopographicPlacesFilter;
import no.entur.mummu.util.TransportModesFilter;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;
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
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
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
            @RequestParam(required = false) List<VehicleModeEnumeration> transportModes,
            @RequestParam(required = false) List<StopTypeEnumeration> stopPlaceTypes,
            @RequestParam(required = false) List<String> topographicPlaceIds
    ) {
        return netexEntitiesIndex.getStopPlaceIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new TransportModesFilter(transportModes))
                .filter(new StopPlaceTypesFilter(stopPlaceTypes))
                .filter(new TopographicPlacesFilter(topographicPlaceIds, netexEntitiesIndex.getTopographicPlaceIndex()))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/json")
    public Collection<StopPlace> getStopPlaceVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/json")
    public StopPlace getStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getVersion(id, version)
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
                .map(key -> netexEntitiesIndex.getQuayIndex().getLatestVersion(key))
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/quays/{id}", produces = "application/json")
    public Quay getQuayById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/quays/{id}/versions", produces = "application/json")
    public Collection<Quay> getQuayVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/quays/{id}/versions/{version}", produces = "application/json")
    public Quay getQuayVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getVersion(id, version)
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
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
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
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
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
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/json")
    public List<GroupOfTariffZones> getGroupsOfTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfTariffZonesIndex().getAll().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/json")
    public GroupOfTariffZones getGroupOfTariffZonesById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getGroupOfTariffZonesIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones", produces = "application/json")
    public List<FareZone> getFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getFareZoneIndex().getAll().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }
}
