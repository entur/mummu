package no.entur.mummu.resources;

import no.entur.mummu.services.NetexEntitiesService;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBElement;

@RestController
public class XmlRestResource {
    private final NetexEntitiesService netexEntitiesService;

    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    @Autowired
    public XmlRestResource(
            NetexEntitiesService netexEntitiesService
    ) {
        this.netexEntitiesService = netexEntitiesService;
    }

    @GetMapping(value = "/stop-places/{id}", produces = "application/xml")
    public JAXBElement<StopPlace> getPublicationDeliveryStopPlaceById(@PathVariable String id) {
        return netexObjectFactory.createStopPlace(netexEntitiesService.getStopPlace(id));
    }
}
