package no.entur.mummu.services;

import no.entur.mummu.resources.NotFoundException;
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
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NetexEntitiesService {
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public NetexEntitiesService(
            NetexEntitiesIndex netexEntitiesIndex
    ) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    public List<GroupOfStopPlaces> getGroupsOfStopPlaces(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfStopPlacesIndex().getAll().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public GroupOfStopPlaces getGroupOfStopPlaces(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getGroupOfStopPlacesIndex().get(id)
        ).orElseThrow(NotFoundException::new);
    }

    public List<StopPlace> getStopPlaces(
            Integer count,
            Integer skip,
            List<String> ids,
            List<VehicleModeEnumeration> transportModes,
            List<StopTypeEnumeration> stopPlaceTypes,
            List<String> topographicPlaceIds
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

    public StopPlace getStopPlace(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public List<StopPlace> getStopPlaceVersions(String id) {
        return new ArrayList<>(Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new));
    }

    public StopPlace getStopPlaceVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    public List<TariffZone> getTariffZones(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getTariffZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public TariffZone getTariffZone(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public List<TariffZone> getTariffZoneVersions(String id) {
        return new ArrayList<>(Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new));
    }

    public TariffZone getTariffZoneVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTariffZoneIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    public List<GroupOfTariffZones> getGroupsOfTariffZones(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfTariffZonesIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public GroupOfTariffZones getGroupOfTariffZonesById(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getGroupOfTariffZonesIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public List<FareZone> getFareZones(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getFareZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public FareZone getFareZone(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public List<FareZone> getFareZoneVersions(String id) {
        return new ArrayList<>(Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new));
    }

    public FareZone getFareZoneVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getFareZoneIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }
}
