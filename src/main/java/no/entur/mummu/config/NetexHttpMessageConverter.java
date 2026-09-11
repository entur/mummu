package no.entur.mummu.config;

import jakarta.xml.bind.JAXBContext;
import no.entur.mummu.resources.ErrorResponse;
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

    /**
     * Error bodies get their own context rather than joining the NeTEx one.
     * <p>
     * {@link #supports} answers for every class, so this converter — not Jackson
     * — is what Spring picks to write an {@link ErrorResponse} under
     * {@code Accept: application/xml}. The context therefore has to know the
     * type, or marshalling throws mid-write and the container turns the intended
     * 404 or 400 into a 500.
     * <p>
     * Adding it to the NeTEx context instead would work, but ErrorResponse is in
     * no namespace and would claim the default one, pushing every NeTEx element
     * onto a generated prefix ({@code <ns4:StopPlace>} where clients have always
     * been served {@code <StopPlace>}). Keeping the contexts apart keeps the
     * NeTEx wire format byte-for-byte what it was.
     */
    private static final JAXBContext errorContext = createContext(ErrorResponse.class);

    private Marshaller marshaller;

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) {
        return null;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        createMarshaller(contextFor(o)).marshal(o, result);
    }

    private static JAXBContext contextFor(Object o) {
        return o instanceof ErrorResponse ? errorContext : publicationDeliveryContext;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return List.of(MediaType.APPLICATION_XML);
    }

    private Marshaller createMarshaller(JAXBContext context) throws JAXBException {
        Marshaller marshallerInstance = context.createMarshaller();
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
