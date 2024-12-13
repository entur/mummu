package no.entur.mummu.resources;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.xml.bind.JAXBElement;
import no.entur.mummu.services.NetexEntitiesService;
import no.entur.mummu.services.NetexObjectFactory;
import no.entur.mummu.util.MultimodalFilter;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;


@RestController
public class RestResource {
    private final NetexEntitiesService netexEntitiesService;
    private static final NetexObjectFactory netexObjectFactory = new NetexObjectFactory();


    @Autowired
    public RestResource(
            NetexEntitiesService netexEntitiesService
    ) {
        this.netexEntitiesService = netexEntitiesService;
    }

    @GetMapping(value = "/groups-of-stop-places", produces = "application/json")
    public List<GroupOfStopPlaces> getGroupOfStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getGroupsOfStopPlaces(count, skip, ids);
    }

    @GetMapping(value = "/groups-of-stop-places", produces = "application/xml")
    public JAXBElement<GroupsOfStopPlacesInFrame_RelStructure> getJAXBElementGroupOfStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var groupsOfStopPlaces = netexEntitiesService.getGroupsOfStopPlaces(count, skip, ids);
        return netexObjectFactory.createGroupsOfStopPlaces(groupsOfStopPlaces);
    }

    @GetMapping(value = "/groups-of-stop-places/{id}", produces = {"application/json", "application/xml"})
    public GroupOfStopPlaces getGroupOfStopPlacesById(@PathVariable String id) {
        return netexEntitiesService.getGroupOfStopPlaces(id);
    }

    @GetMapping(value = "/stop-places", produces = "application/json")
    public Collection<StopPlace> getStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false, defaultValue = "both") MultimodalFilter.MultimodalFilterType multimodal,
            @RequestParam(required = false) List<VehicleModeEnumeration> transportModes,
            @RequestParam(required = false) List<StopTypeEnumeration> stopPlaceTypes,
            @RequestParam(required = false) List<String> topographicPlaceIds
    ) {
        return netexEntitiesService.getStopPlaces(count, skip, ids, multimodal, transportModes, stopPlaceTypes, topographicPlaceIds);
    }

    @GetMapping(value = "/stop-places", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false, defaultValue = "both") MultimodalFilter.MultimodalFilterType multimodal,
            @RequestParam(required = false) List<VehicleModeEnumeration> transportModes,
            @RequestParam(required = false) List<StopTypeEnumeration> stopPlaceTypes,
            @RequestParam(required = false) List<String> topographicPlaceIds
    ) {
        var stopPlaces = netexEntitiesService.getStopPlaces(count, skip, ids, multimodal, transportModes, stopPlaceTypes, topographicPlaceIds);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }


    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(@PathVariable String id) {
        return netexEntitiesService.getStopPlace(id);
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceById(@PathVariable String id) {
        var stopPlace = netexEntitiesService.getStopPlace(id);
        return netexObjectFactory.createStopPlace(stopPlace);
    }

    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/json")
    public Collection<StopPlace> getStopPlaceVersions(@PathVariable String id) {
        return netexEntitiesService.getStopPlaceVersions(id);
    }

    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaceVersions(@PathVariable String id) {
        var stopPlaces = netexEntitiesService.getStopPlaceVersions(id);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }

    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/json")
    public StopPlace getStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getStopPlaceVersion(id, version);
    }

    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        var stopPlace = netexEntitiesService.getStopPlaceVersion(id, version);
        return netexObjectFactory.createStopPlace(stopPlace);
    }

    @GetMapping(value = "/stop-places/{id}/children", produces = "application/json")
    public Collection<StopPlace> getStopPlaceChildren(@PathVariable String id) {
        return netexEntitiesService.getStopPlaceChildren(id);
    }

    @GetMapping(value = "/stop-places/{id}/children", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaceChildren(@PathVariable String id) {
        var stopPlaces = netexEntitiesService.getStopPlaceChildren(id);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }

    @GetMapping(value="/stop-places/{id}/parkings", produces = "application/json")
    public Collection<Parking> getParkingByStopPlaceId(@PathVariable String id) {
        return netexEntitiesService.getParkingByStopPlaceId(id);
    }

    @GetMapping(value="/stop-places/{id}/parkings", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkingByStopPlaceId(@PathVariable String id) {
        var parkings = netexEntitiesService.getParkingByStopPlaceId(id);
        return netexObjectFactory.createParkings(parkings);
    }

    @GetMapping(value = "/quays", produces = "application/json")
    public List<Quay> getQuays(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getQuays(count, skip, ids);
    }

    @GetMapping(value = "/quays/{id}", produces = "application/json")
    public Quay getQuayById(@PathVariable String id) {
        return netexEntitiesService.getQuayById(id);
    }

    @GetMapping(value = "/quays/{id}", produces = "application/xml")
    public JAXBElement<Quay> getJAXBElementQuayById(@PathVariable String id) {
        return netexObjectFactory.createQuay(netexEntitiesService.getQuayById(id));
    }

    @GetMapping(value = "/quays/{id}/versions", produces = "application/json")
    public Collection<Quay> getQuayVersions(@PathVariable String id) {
        return netexEntitiesService.getQuayVersions(id);
    }

    @GetMapping(value = "/quays/{id}/versions/{version}", produces = "application/json")
    public Quay getQuayVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getQuayVersion(id, version);
    }

    @GetMapping(value = "/quays/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<Quay> getJAXBElementQuayVersion(@PathVariable String id, @PathVariable String version) {
        return netexObjectFactory.createQuay(netexEntitiesService.getQuayVersion(id, version));
    }

    @GetMapping(value = "quays/{id}/stop-place", produces = "application/json")
    public StopPlace getStopPlaceByQuayId(@PathVariable String id) {
        return netexEntitiesService.getStopPlaceByQuayId(id);
    }

    @GetMapping(value = "quays/{id}/stop-place", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceByQuayId(@PathVariable String id) {
        return netexObjectFactory.createStopPlace(netexEntitiesService.getStopPlaceByQuayId(id));
    }

    @GetMapping(value = "/parkings", produces = "application/json")
    public List<Parking> getParkings(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getParkings(count, skip, ids);
    }

    @GetMapping(value = "/parkings", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkings(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var parkings = netexEntitiesService.getParkings(count, skip, ids);
        return netexObjectFactory.createParkings(parkings);
    }

    @GetMapping(value = "/parkings/{id}", produces = "application/json")
    public Parking getParkingById(@PathVariable String id) {
        return netexEntitiesService.getParkingById(id);
    }

    @GetMapping(value = "/parkings/{id}", produces = "application/xml")
    public JAXBElement<Parking> getJAXBElementParkingById(@PathVariable String id) {
        return netexObjectFactory.createParking(netexEntitiesService.getParkingById(id));
    }

    @GetMapping(value = "/parkings/{id}/versions", produces = "application/json")
    public Collection<Parking> getParkingVersions(@PathVariable String id) {
        return netexEntitiesService.getParkingVersions(id);
    }

    @GetMapping(value = "/parkings/{id}/versions", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkingVersions(@PathVariable String id) {
        var parkings = netexEntitiesService.getParkingVersions(id);
        return netexObjectFactory.createParkings(parkings);
    }

    @GetMapping(value = "/parkings/{id}/versions/{version}", produces = "application/json")
    public Parking getParkingVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getParkingVersion(id, version);
    }

    @GetMapping(value = "/parkings/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<Parking> getJAXBElementParkingVersion(@PathVariable String id, @PathVariable String version) {
        return netexObjectFactory.createParking(netexEntitiesService.getParkingVersion(id, version));
    }

    @GetMapping(value = "/topographic-places", produces ="application/json")
    public List<TopographicPlace> getTopographicPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getTopographicPlaces(count, skip, ids);
    }

    @GetMapping(value = "/topographic-places", produces ="application/xml")
    public JAXBElement<TopographicPlacesInFrame_RelStructure> getJAXBElementTopographicPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var topographicPlaces = netexEntitiesService.getTopographicPlaces(count, skip, ids);
        return netexObjectFactory.createTopographicPlaces(topographicPlaces);
    }

    @GetMapping(value = "/topographic-places/{id}", produces = "application/json")
    public TopographicPlace getTopographicPlaceById(@PathVariable String id) {
        return netexEntitiesService.getTopographicPlaceById(id);
    }

    @GetMapping(value = "/topographic-places/{id}", produces = "application/xml")
    public JAXBElement<TopographicPlace> getJAXBElementTopographicPlaceById(@PathVariable String id) {
        var topographicPlace = netexEntitiesService.getTopographicPlaceById(id);
        return netexObjectFactory.createTopographicPlace(topographicPlace);
    }

    @GetMapping(value = "/topographic-places/{id}/versions", produces = "application/json")
    public Collection<TopographicPlace> getTopographicPlaceVersions(@PathVariable String id) {
        return netexEntitiesService.getTopographicPlaceVersions(id);
    }

    @GetMapping(value = "/topographic-places/{id}/versions", produces = "application/xml")
    public JAXBElement<TopographicPlacesInFrame_RelStructure> getJAXBElementTopographicPlaceVersions(@PathVariable String id) {
        var topographicPlaces = netexEntitiesService.getTopographicPlaceVersions(id);
        return netexObjectFactory.createTopographicPlaces(topographicPlaces);
    }

    @GetMapping(value = "/topographic-places/{id}/versions/{version}", produces = "application/json")
    public TopographicPlace getTopographicPlaceVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getTopographicPlaceVersion(id, version);
    }

    @GetMapping(value = "/topographic-places/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<TopographicPlace> getJAXBElementTopographicPlaceVersion(@PathVariable String id, @PathVariable String version) {
        var topographicPlace = netexEntitiesService.getTopographicPlaceVersion(id, version);
        return netexObjectFactory.createTopographicPlace(topographicPlace);
    }

    @GetMapping(value = "/tariff-zones", produces = "application/json")
    @Operation(deprecated = true)
    public List<TariffZone> getTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        return netexEntitiesService.getTariffZones(count, skip, ids, authorityRefs);
    }

    @GetMapping(value = "/tariff-zones", produces = "application/xml")
    @Operation(deprecated = true)
    public JAXBElement<TariffZonesInFrame_RelStructure> getJAXBElementTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        var tariffZones = netexEntitiesService.getTariffZones(count, skip, ids, authorityRefs);
        return netexObjectFactory.createTariffZones(tariffZones);
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    @Operation(deprecated = true)
    public TariffZone getTariffZoneById(@PathVariable String id) {
        return netexEntitiesService.getTariffZone(id);
    }

    @GetMapping(value = "/tariff-zones/{id}", produces = "application/xml")
    @Operation(deprecated = true)
    public JAXBElement<TariffZone> getJAXBElementTariffZoneById(@PathVariable String id) {
        var tariffZone = netexEntitiesService.getTariffZone(id);
        return netexObjectFactory.createTariffZone(tariffZone);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions", produces = "application/json")
    @Operation(deprecated = true)
    public Collection<TariffZone> getTariffZoneVersions(@PathVariable String id) {
        return netexEntitiesService.getTariffZoneVersions(id);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions", produces = "application/xml")
    public JAXBElement<TariffZonesInFrame_RelStructure> getJAXBElementTariffZoneVersions(@PathVariable String id) {
        var tariffZones = netexEntitiesService.getTariffZoneVersions(id);
        return netexObjectFactory.createTariffZones(tariffZones);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions/{version}", produces = "application/json")
    @Operation(deprecated = true)
    public TariffZone getTariffZoneVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getTariffZoneVersion(id, version);
    }

    @GetMapping(value = "/tariff-zones/{id}/versions/{version}", produces = "application/xml")
    @Operation(deprecated = true)
    public JAXBElement<TariffZone> getJAXBElementTariffZoneVersion(@PathVariable String id, @PathVariable String version) {
        var tariffZone = netexEntitiesService.getTariffZoneVersion(id, version);
        return netexObjectFactory.createTariffZone(tariffZone);
    }

    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/json")
    public List<GroupOfTariffZones> getGroupsOfTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getGroupsOfTariffZones(count, skip, ids);
    }

    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/xml")
    public JAXBElement<GroupsOfTariffZonesInFrame_RelStructure> getJAXBElementGroupsOfTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var groupsOfTariffZones = netexEntitiesService.getGroupsOfTariffZones(count, skip, ids);
        return netexObjectFactory.createGroupsOfTariffZones(groupsOfTariffZones);
    }

    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/json")
    public GroupOfTariffZones getGroupOfTariffZonesById(@PathVariable String id) {
        return netexEntitiesService.getGroupOfTariffZonesById(id);
    }

    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/xml")
    public JAXBElement<GroupOfTariffZones> getJAXBElementGroupOfTariffZonesById(@PathVariable String id) {
        var groupOfTariffZones = netexEntitiesService.getGroupOfTariffZonesById(id);
        return netexObjectFactory.createGroupOfTariffZones(groupOfTariffZones);
    }

    @GetMapping(value = "/fare-zones", produces = "application/json")
    public List<FareZone> getFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        return netexEntitiesService.getFareZones(count, skip, ids, authorityRefs);
    }

    @GetMapping(value = "/fare-zones", produces = "application/xml")
    public JAXBElement<FareZonesInFrame_RelStructure> getJAXBElementFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        var fareZones = netexEntitiesService.getFareZones(count, skip, ids, authorityRefs);
        return netexObjectFactory.createFareZones(fareZones);
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(@PathVariable String id) {
        return netexEntitiesService.getFareZone(id);
    }

    @GetMapping(value = "/fare-zones/{id}", produces = "application/xml")
    public JAXBElement<FareZone> getJAXBElementFareZoneById(@PathVariable String id) {
        return netexObjectFactory.createFareZone(netexEntitiesService.getFareZone(id));
    }

    @GetMapping(value = "/fare-zones/{id}/versions", produces = "application/json")
    public Collection<FareZone> getFareZoneVersions(@PathVariable String id) {
        return netexEntitiesService.getFareZoneVersions(id);
    }

    @GetMapping(value = "/fare-zones/{id}/versions", produces = "application/xml")
    public JAXBElement<FareZonesInFrame_RelStructure> getJAXBElementFareZoneVersions(@PathVariable String id) {
        var fareZones = netexEntitiesService.getFareZoneVersions(id);
        return netexObjectFactory.createFareZones(fareZones);
    }

    @GetMapping(value = "/fare-zones/{id}/versions/{version}", produces = "application/json")
    public FareZone getFareZoneVersion(@PathVariable String id, @PathVariable String version) {
        return netexEntitiesService.getFareZoneVersion(id, version);
    }

    @GetMapping(value = "/fare-zones/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<FareZone> getJAXBElementFareZoneVersion(@PathVariable String id, @PathVariable String version) {
        var fareZone = netexEntitiesService.getFareZoneVersion(id, version);
        return netexObjectFactory.createFareZone(fareZone);
    }
}
