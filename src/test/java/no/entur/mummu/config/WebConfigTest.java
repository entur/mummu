package no.entur.mummu.config;

import no.entur.mummu.MummuApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
class WebConfigTest {

    @Autowired
    private WebConfig webConfig;

    private List<Class<?>> converterTypes() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        webConfig.configureMessageConverters(converters);
        return converters.stream().<Class<?>>map(HttpMessageConverter::getClass).toList();
    }

    @Test
    void prefersPreRenderedFragmentsOverSerializingWithJackson() {
        List<Class<?>> types = converterTypes();

        int fragments = types.indexOf(NetexJsonFragmentHttpMessageConverter.class);
        int jackson = types.indexOf(MappingJackson2HttpMessageConverter.class);

        assertTrue(fragments >= 0, "fragment converter is not registered: " + types);
        assertTrue(jackson >= 0, "Jackson converter is not registered: " + types);
        assertTrue(fragments < jackson,
                "fragment converter must come before Jackson to take precedence, got " + types);
    }
}
