package no.entur.mummu.config;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;

public class NetexHttpMessageConverter extends AbstractXmlHttpMessageConverter<Object> {
    private static final JAXBContext publicationDeliveryContext = createContext(PublicationDeliveryStructure.class);
    private Marshaller marshaller;

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws Exception {
        return null;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        getMarshaller().marshal(o, result);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return List.of(MediaType.APPLICATION_XML);
    }

    private Marshaller getMarshaller() throws JAXBException, IOException, SAXException {
        if (marshaller == null) {
            marshaller = createMarshaller();
        }

        return marshaller;
    }

    private Marshaller createMarshaller() throws JAXBException, IOException, SAXException {
        Marshaller marshaller = publicationDeliveryContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");


        return marshaller;
    }

    private static JAXBContext createContext(Class... clazz) {
        try {
            JAXBContext jaxbContext = newInstance(clazz);
            //logger.info("Created context {}", jaxbContext.getClass());
            return jaxbContext;
        } catch (JAXBException e) {
            String message = "Could not create instance of jaxb context for class " + clazz;
            //logger.warn(message, e);
            throw new RuntimeException("Could not create instance of jaxb context for class " + clazz, e);
        }
    }
}
