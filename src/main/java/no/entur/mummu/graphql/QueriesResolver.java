package no.entur.mummu.graphql;

import no.entur.mummu.graphql.api.StopPlaceRegisterResolver;
import no.entur.mummu.graphql.model.AuthorizationCheck;
import no.entur.mummu.graphql.model.FareZone;
import no.entur.mummu.graphql.model.GroupOfStopPlaces;
import no.entur.mummu.graphql.model.GroupOfTariffZones;
import no.entur.mummu.graphql.model.Parking;
import no.entur.mummu.graphql.model.PathLink;
import no.entur.mummu.graphql.model.ScopingMethodEnumerationType;
import no.entur.mummu.graphql.model.StopPlace;
import no.entur.mummu.graphql.model.StopPlaceInterface;
import no.entur.mummu.graphql.model.StopPlaceRegister;
import no.entur.mummu.graphql.model.StopPlaceType;
import no.entur.mummu.graphql.model.Tag;
import no.entur.mummu.graphql.model.TariffZone;
import no.entur.mummu.graphql.model.TopographicPlace;
import no.entur.mummu.graphql.model.TopographicPlaceType;
import no.entur.mummu.graphql.model.VersionValidity;
import no.entur.mummu.graphql.model.ZoneTopologyEnumerationType;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class QueriesResolver implements StopPlaceRegisterResolver {
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public QueriesResolver(
            NetexEntitiesIndexLoader loader
    ) {
        this.netexEntitiesIndex = loader.getNetexEntitiesIndex();
    }

    @Override
    public List<StopPlaceInterface> stopPlace(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, Boolean allVersions, String id, Integer version, VersionValidity versionValidity, List<StopPlaceType> stopPlaceType, List<String> countyReference, List<String> countryReference, List<String> tags, List<String> municipalityReference, String query, String importedId, Date pointInTime, String key, Boolean withoutLocationOnly, Boolean withoutQuaysOnly, Boolean withDuplicatedQuayImportedIds, Boolean withNearbySimilarDuplicates, Boolean hasParking, Boolean onlyMonomodalStopPlaces, List<String> values, Boolean withTags, String code) throws Exception {
        var index = netexEntitiesIndex.getStopPlaceIndex();
        var stopPlace = index.getLatestVersion(id);
        return List.of(
                StopPlace.builder()
                        .setId(stopPlace.getId())
                        .build()
        );
    }

    @Override
    public List<StopPlaceInterface> stopPlaceBBox(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String lonMin, String latMin, String lonMax, String latMax, String ignoreStopPlaceId, Boolean includeExpired, Date pointInTime) throws Exception {
        return null;
    }

    @Override
    public List<TopographicPlace> topographicPlace(StopPlaceRegister stopPlaceRegister, String id, Boolean allVersions, TopographicPlaceType topographicPlaceType, String query) throws Exception {
        return null;
    }

    @Override
    public List<PathLink> pathLink(StopPlaceRegister stopPlaceRegister, String id, Boolean allVersions, String stopPlaceId) throws Exception {
        return null;
    }

    @Override
    public List<Parking> parking(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String id, Integer version, String stopPlaceId, Boolean allVersions) throws Exception {
        return null;
    }

    @Override
    public AuthorizationCheck checkAuthorized(StopPlaceRegister stopPlaceRegister, String id) throws Exception {
        return null;
    }

    @Override
    public List<Tag> tags(StopPlaceRegister stopPlaceRegister, String name) throws Exception {
        return null;
    }

    @Override
    public List<GroupOfStopPlaces> groupOfStopPlaces(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String id, String query, String stopPlaceId) throws Exception {
        return null;
    }

    @Override
    public List<GroupOfTariffZones> groupOfTariffZones(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String id, String query, String tariffZoneId) throws Exception {
        return null;
    }

    @Override
    public List<TariffZone> tariffZones(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String query) throws Exception {
        return null;
    }

    @Override
    public List<FareZone> fareZones(StopPlaceRegister stopPlaceRegister, Integer page, Integer size, String query, String id, String authorityRef, ScopingMethodEnumerationType scopingMethod, ZoneTopologyEnumerationType zoneTopology) throws Exception {
        return null;
    }
}
