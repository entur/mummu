package no.entur.mummu.resources;

import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.Zone_VersionStructure;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NetexObjectFactory extends ObjectFactory {

    private final static QName _stopPlaces_QNAME = new QName("http://www.netex.org.uk/netex", "stopPlaces");
    private final static QName _groupsOfStopPlaces_QNAME = new QName("http://www.netex.org.uk/netex", "groupsOfStopPlaces");
    private final static QName _fareZones_QNAME = new QName("http://www.netex.org.uk/netex", "fareZones");
    private final static QName _tariffZones_QNAME = new QName("http://www.netex.org.uk/netex", "tariffZones");
    private final static QName _groupsOfTariffZones_QNAME = new QName("http://www.netex.org.uk/netex", "groupsOfTariffZones");

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
}
