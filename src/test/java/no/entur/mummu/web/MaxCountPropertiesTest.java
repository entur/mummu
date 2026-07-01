package no.entur.mummu.web;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxCountPropertiesTest {

    @Test
    void maxForReturnsDefaultWhenNoOverride() {
        MaxCountProperties props = new MaxCountProperties();
        props.setMaxCount(1000);

        assertEquals(1000, props.maxFor("stop-places"));
    }

    @Test
    void maxForReturnsOverrideWhenPresent() {
        MaxCountProperties props = new MaxCountProperties();
        props.setMaxCount(1000);
        props.setMaxCountOverrides(Map.of("fare-zones", 9999L));

        assertEquals(9999, props.maxFor("fare-zones"));
        assertEquals(1000, props.maxFor("stop-places"));
    }
}
