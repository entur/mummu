package no.entur.mummu.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.entur.mummu.MummuApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "no.entur.mummu.logging.large-response-threshold-bytes=10")
class LargeResponseLoggingFilterIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private Logger filterLogger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        filterLogger = (Logger) LoggerFactory.getLogger(LargeResponseLoggingFilter.class);
        appender = new ListAppender<>();
        appender.start();
        filterLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        filterLogger.detachAppender(appender);
    }

    @Test
    void logsLargeResponseWithPath() throws Exception {
        mvc.perform(get("/stop-places").accept("application/json"))
                .andExpect(status().isOk());

        boolean logged = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(m -> m.contains("Large response completed") && m.contains("/stop-places"));

        Assertions.assertTrue(logged, "expected a large-response log line for /stop-places; got: "
                + appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList());
    }
}
