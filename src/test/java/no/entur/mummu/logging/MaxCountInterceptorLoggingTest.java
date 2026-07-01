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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "no.entur.mummu.list.enforce-max-count=false",
        "no.entur.mummu.list.max-count=5"
})
class MaxCountInterceptorLoggingTest {

    @Autowired
    private MockMvc mvc;

    private Logger interceptorLogger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        interceptorLogger = (Logger) LoggerFactory.getLogger(MaxCountInterceptor.class);
        appender = new ListAppender<>();
        appender.start();
        interceptorLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        interceptorLogger.detachAppender(appender);
    }

    @Test
    void logsButServesWhenNotEnforcing() throws Exception {
        mvc.perform(get("/stop-places?count=10").accept(MediaType.APPLICATION_JSON)
                        .header("ET-Client-Name", "bulk-client"))
                .andExpect(status().isOk());

        boolean logged = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(m -> m.contains("Request count 10 exceeds max 5 for /stop-places")
                        && m.contains("client=bulk-client"));

        Assertions.assertTrue(logged, "expected an over-max WARN when not enforcing; got: "
                + appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList());
    }
}
