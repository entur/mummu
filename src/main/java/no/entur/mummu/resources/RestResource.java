package no.entur.mummu.resources;

import no.entur.mummu.services.NetexEntitiesService;
import no.entur.mummu.util.NetexIdComparator;
import no.entur.mummu.util.NetexIdFilter;
import no.entur.mummu.util.NetexTechnicalIdComparator;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RestResource {
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final NetexEntitiesService netexEntitiesService;
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final static QName _stopPlaces_QNAME = new QName("http://www.netex.org.uk/netex", "stopPlaces");

    @Autowired
    public RestResource(
            NetexEntitiesIndex netexEntitiesIndex,
            NetexEntitiesService netexEntitiesService
    ) {
        this.netexEntitiesIndex = netexEntitiesIndex;
        this.netexEntitiesService = netexEntitiesService;
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
        return netexEntitiesService.getStopPlaces(count, skip, ids, transportModes, stopPlaceTypes, topographicPlaceIds);
    }

    @GetMapping(value = "/stop-places", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getPublicationDeliveryStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<VehicleModeEnumeration> transportModes,
            @RequestParam(required = false) List<StopTypeEnumeration> stopPlaceTypes,
            @RequestParam(required = false) List<String> topographicPlaceIds
    ) {
        var stopPlaces = netexObjectFactory.createStopPlacesInFrame_RelStructure().withStopPlace(
                netexEntitiesService.getStopPlaces(count, skip, ids, transportModes, stopPlaceTypes, topographicPlaceIds)
        );

        return new JAXBElement<>(_stopPlaces_QNAME, StopPlacesInFrame_RelStructure.class, stopPlaces);
    }


    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return netexEntitiesService.getStopPlace(id);
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/xml")
    public JAXBElement<StopPlace> getPublicationDeliveryStopPlaceById(@PathVariable String id) {
        var stopPlace = netexEntitiesService.getStopPlace(id);
        return netexObjectFactory.createStopPlace(stopPlace);
    }

    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/json")
    public Collection<StopPlace> getStopPlaceVersions(@PathVariable String id) {
        return netexEntitiesService.getStopPlaceVersions(id);
    }

    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getPublicationDeliveryStopPlaceVersions(@PathVariable String id) {
        var stopPlaces = netexObjectFactory.createStopPlacesInFrame_RelStructure().withStopPlace(
                netexEntitiesService.getStopPlaceVersions(id)
        );

        return new JAXBElement<>(_stopPlaces_QNAME, StopPlacesInFrame_RelStructure.class, stopPlaces);
    }

    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/json")
    public StopPlace getStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getStopPlaceVersion(id, version);
    }

    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<StopPlace> getPublicationDeliveryStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        var stopPlace = netexEntitiesService.getStopPlaceVersion(id, version);
        return netexObjectFactory.createStopPlace(stopPlace);
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
        return netexEntitiesIndex.getQuayIndex().getLatestVersions().stream()
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
        return netexEntitiesIndex.getParkingIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/parkings/{id}", produces = "application/json")
    public Parking getParkingById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/parkings/{id}/versions", produces = "application/json")
    public Collection<Parking> getParkingVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/parkings/{id}/versions/{version}", produces = "application/json")
    public Parking getParkingVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/topographic-places", produces ="application/json")
    public List<TopographicPlace> getTopographicPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getTopographicPlaceIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/topographic-places/{id}", produces = "application/json")
    public TopographicPlace getTopographicPlaceById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/topographic-places/{id}/versions", produces = "application/json")
    public Collection<TopographicPlace> getTopographicPlaceVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/topographic-places/{id}/versions/{version}", produces = "application/json")
    public TopographicPlace getTopographicPlaceVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/tariff-zones", produces = "application/json")
    public List<TariffZone> getTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getTariffZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions", produces = "application/json")
    public Collection<TariffZone> getTariffZoneVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions/{version}", produces = "application/json")
    public TariffZone getTariffZoneVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/json")
    public List<GroupOfTariffZones> getGroupsOfTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfTariffZonesIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/json")
    public GroupOfTariffZones getGroupOfTariffZonesById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getGroupOfTariffZonesIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones", produces = "application/json")
    public List<FareZone> getFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesIndex.getFareZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones/{id}/versions", produces = "application/json")
    public Collection<FareZone> getFareZoneVersions(@PathVariable String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/fare-zones/{id}/versions/{version}", produces = "application/json")
    public FareZone getFareZoneVersion(@PathVariable String id, @PathVariable String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    /**
     * Set field value with reflection.
     * Used for setting list values in netex model.
     */
    private void setField(Class clazz, String fieldName, Object instance, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Cannot set field " + fieldName + " of " + instance, e);
        }
    }


}
