package no.entur.mummu.services;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.Zone_VersionStructure;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NetexObjectFactory extends ObjectFactory {

    public static final String NAMESPACE_URI = "http://www.netex.org.uk/netex";
    private static final QName _stopPlaces_QNAME = new QName(NAMESPACE_URI, "stopPlaces");
    private static final QName _groupsOfStopPlaces_QNAME = new QName(NAMESPACE_URI, "groupsOfStopPlaces");
    private static final QName _fareZones_QNAME = new QName(NAMESPACE_URI, "fareZones");
    private static final QName _tariffZones_QNAME = new QName(NAMESPACE_URI, "tariffZones");
    private static final QName _groupsOfTariffZones_QNAME = new QName(NAMESPACE_URI, "groupsOfTariffZones");
    private static final QName _parkings_QNAME = new QName(NAMESPACE_URI, "parkings");
    private static final QName _topographicPlaces_QNAME = new QName(NAMESPACE_URI, "topographicPlaces");

    public JAXBElement<GroupsOfStopPlacesInFrame_RelStructure> createGroupsOfStopPlaces(List<GroupOfStopPlaces> groupsOfStopPlaces) {
        var groupsOfStopPlacesInFrame = createGroupsOfStopPlacesInFrame_RelStructure().withGroupOfStopPlaces(groupsOfStopPlaces);
        return new JAXBElement<>(_groupsOfStopPlaces_QNAME, GroupsOfStopPlacesInFrame_RelStructure.class, groupsOfStopPlacesInFrame);
    }

    public JAXBElement<StopPlacesInFrame_RelStructure> createStopPlaces(List<StopPlace> stopPlaces) {
        var stopPlacesInFrame = createStopPlacesInFrame_RelStructure().withStopPlace(stopPlaces);
        return new JAXBElement<>(_stopPlaces_QNAME, StopPlacesInFrame_RelStructure.class, stopPlacesInFrame);
    }

    public JAXBElement<TariffZonesInFrame_RelStructure> createTariffZones(List<TariffZone> tariffZones) {
        Collection<JAXBElement<? extends Zone_VersionStructure>> elements = tariffZones
               .stream()
                .map(this::createTariffZone)
                .collect(Collectors.toList());
        var tariffZonesInFrame = createTariffZonesInFrame_RelStructure().withTariffZone(elements);
        return new JAXBElement<>(_tariffZones_QNAME, TariffZonesInFrame_RelStructure.class, tariffZonesInFrame);
    }

    public JAXBElement<GroupsOfTariffZonesInFrame_RelStructure> createGroupsOfTariffZones(List<GroupOfTariffZones> groupsOfTariffZones) {
        var groupsOfTariffZonesInFrame = createGroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(groupsOfTariffZones);
        return new JAXBElement<>(_groupsOfTariffZones_QNAME, GroupsOfTariffZonesInFrame_RelStructure.class, groupsOfTariffZonesInFrame);
    }

    public JAXBElement<FareZonesInFrame_RelStructure> createFareZones(List<FareZone> fareZones) {
        var fareZonesInFrame = createFareZonesInFrame_RelStructure().withFareZone(fareZones);
        return new JAXBElement<>(_fareZones_QNAME, FareZonesInFrame_RelStructure.class, fareZonesInFrame);
    }

    public JAXBElement<ParkingsInFrame_RelStructure> createParkings(Collection<Parking> parkings) {
        var parkingsInFrame = createParkingsInFrame_RelStructure().withParking(parkings);
        return new JAXBElement<>(_parkings_QNAME, ParkingsInFrame_RelStructure.class, parkingsInFrame);
    }

    public JAXBElement<TopographicPlacesInFrame_RelStructure> createTopographicPlaces(Collection<TopographicPlace> topographicPlaces) {
        var topographicPlacesInFrame = createTopographicPlacesInFrame_RelStructure().withTopographicPlace(topographicPlaces);
        return new JAXBElement<>(_topographicPlaces_QNAME, TopographicPlacesInFrame_RelStructure.class, topographicPlacesInFrame);
    }
}
