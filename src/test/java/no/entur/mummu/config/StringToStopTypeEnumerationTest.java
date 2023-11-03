package no.entur.mummu.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.text.ParseException;
import java.util.Locale;

class StringToStopTypeEnumerationTest {

    @Test
    void testParse() throws ParseException {
        var formatter = new StringToStopTypeEnumeration();
        Assertions.assertEquals(StopTypeEnumeration.BUS_STATION, formatter.parse("busStation", Locale.getDefault()));
        Assertions.assertEquals(StopTypeEnumeration.BUS_STATION, formatter.parse("BUS_STATION", Locale.getDefault()));
    }

    @Test
    void testPrint() {
        var formatter = new StringToStopTypeEnumeration();
        Assertions.assertEquals("busStation", formatter.print(StopTypeEnumeration.BUS_STATION, Locale.getDefault()));
    }
}
