package no.entur.mummu.config;

import jakarta.xml.bind.JAXBContext;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.List;

import static jakarta.xml.bind.JAXBContext.newInstance;

public class NetexHttpMessageConverter extends AbstractXmlHttpMessageConverter<Object> {

    private static final Logger log = LoggerFactory.getLogger(NetexHttpMessageConverter.class);
    private static final JAXBContext publicationDeliveryContext = createContext(PublicationDeliveryStructure.class);
    private Marshaller marshaller;

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) {
        return null;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        createMarshaller().marshal(o, result);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return List.of(MediaType.APPLICATION_XML);
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshallerInstance = publicationDeliveryContext.createMarshaller();
        marshallerInstance.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshallerInstance.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
        return marshallerInstance;
    }

    private static JAXBContext createContext(Class... clazz) {
        try {
            JAXBContext jaxbContext = newInstance(clazz);
            log.info("Created context {}", jaxbContext.getClass());
            return jaxbContext;
        } catch (JAXBException e) {
            String message = "Could not create instance of jaxb context for class " + clazz;
            log.warn(message, e);
            throw new RuntimeException("Could not create instance of jaxb context for class " + clazz, e);
        }
    }
}
