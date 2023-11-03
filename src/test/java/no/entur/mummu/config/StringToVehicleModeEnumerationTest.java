package no.entur.mummu.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.text.ParseException;
import java.util.Locale;

class StringToVehicleModeEnumerationTest {

    @Test
    void testParse() throws ParseException {
        var formatter = new StringToVehicleModeEnumeration();
        Assertions.assertEquals(VehicleModeEnumeration.RAIL, formatter.parse("rail", Locale.getDefault()));
        Assertions.assertEquals(VehicleModeEnumeration.RAIL, formatter.parse("RAIL", Locale.getDefault()));
    }

    @Test
    void testPrint() {
        var formatter = new StringToVehicleModeEnumeration();
        Assertions.assertEquals("rail", formatter.print(VehicleModeEnumeration.RAIL, Locale.getDefault()));
    }
}
