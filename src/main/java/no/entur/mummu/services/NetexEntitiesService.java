package no.entur.mummu.services;

import no.entur.mummu.resources.NotFoundException;
import no.entur.mummu.serializers.MummuSerializerContext;
import no.entur.mummu.util.CurrentValidityFilter;
import no.entur.mummu.util.FareZoneAuthorityRefFilter;
import no.entur.mummu.util.MultimodalFilter;
import no.entur.mummu.util.NetexIdComparator;
import no.entur.mummu.util.NetexIdFilter;
import no.entur.mummu.util.NetexTechnicalIdComparator;
import no.entur.mummu.util.StopPlaceTypesFilter;
import no.entur.mummu.util.TariffZoneAuthorityRefFilter;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NetexEntitiesService {
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final MummuSerializerContext mummuSerializerContext;

    @Autowired
    public NetexEntitiesService(
            NetexEntitiesIndexLoader loader,
            MummuSerializerContext mummuSerializerContext
    ) {
        this.netexEntitiesIndex = loader.getNetexEntitiesIndex();
        this.mummuSerializerContext = mummuSerializerContext;
    }

    public List<GroupOfStopPlaces> getGroupsOfStopPlaces(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getGroupOfStopPlacesIndex().getAll().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
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
            MultimodalFilter.MultimodalFilterType multimodalFilterType,
            List<VehicleModeEnumeration> transportModes,
            List<StopTypeEnumeration> stopPlaceTypes,
            List<String> topographicPlaceIds
    ) {
        return netexEntitiesIndex.getStopPlaceIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new MultimodalFilter(multimodalFilterType))
                .filter(new TransportModesFilter(transportModes))
                .filter(new StopPlaceTypesFilter(stopPlaceTypes))
                .filter(new TopographicPlacesFilter(topographicPlaceIds, netexEntitiesIndex.getTopographicPlaceIndex()))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
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

    public List<StopPlace> getStopPlaceChildren(String id) {
        return netexEntitiesIndex.getStopPlaceIndex().getLatestVersions().stream()
                .filter(stopPlace -> {
                    if (stopPlace.getParentSiteRef() != null) {
                        return stopPlace.getParentSiteRef().getRef().equals(id);
                    }
                    return false;
                })
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
                .toList();
    }

    public Collection<Parking> getParkingByStopPlaceId(String id) {
        if (netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id) == null) {
            throw new NotFoundException();
        }
        return netexEntitiesIndex.getParkingsByParentSiteRefIndex().get(id);
    }

    public List<Quay> getQuays(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getQuayIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public Quay getQuayById(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Collection<Quay> getQuayVersions(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Quay getQuayVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getQuayIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    public StopPlace getStopPlaceByQuayId(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIdByQuayIdIndex().get(id)
        ).map(
                stopPlaceId -> netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlaceId)
        ).orElseThrow(NotFoundException::new);
    }

    public List<Parking> getParkings(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getParkingIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
                .sorted(new NetexIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public Parking getParkingById(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Collection<Parking> getParkingVersions(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Parking getParkingVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getParkingIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    public List<TopographicPlace> getTopographicPlaces(
            Integer count,
            Integer skip,
            List<String> ids
    ) {
        return netexEntitiesIndex.getTopographicPlaceIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public TopographicPlace getTopographicPlaceById(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Collection<TopographicPlace> getTopographicPlaceVersions(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    public TopographicPlace getTopographicPlaceVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getTopographicPlaceIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }

    public List<TariffZone> getTariffZones(
            Integer count,
            Integer skip,
            List<String> ids,
            List<String> authorityRefs
    ) {
        return netexEntitiesIndex.getTariffZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new TariffZoneAuthorityRefFilter(authorityRefs))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
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
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
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
            List<String> ids,
            List<String> authorityRefs
    ) {
        return netexEntitiesIndex.getFareZoneIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new FareZoneAuthorityRefFilter(authorityRefs))
                .filter(new CurrentValidityFilter(mummuSerializerContext.getZoneId()))
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
