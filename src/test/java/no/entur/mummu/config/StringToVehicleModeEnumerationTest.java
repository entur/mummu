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
        Assertions.assertEquals(VehicleModeEnumeration.SNOW_AND_ICE, formatter.parse("snowAndIce", Locale.getDefault()));
        Assertions.assertEquals(VehicleModeEnumeration.SNOW_AND_ICE, formatter.parse("SNOW_AND_ICE", Locale.getDefault()));
    }

    @Test
    void testPrint() {
        var formatter = new StringToVehicleModeEnumeration();
        Assertions.assertEquals("rail", formatter.print(VehicleModeEnumeration.RAIL, Locale.getDefault()));
        Assertions.assertEquals("snowAndIce", formatter.print(VehicleModeEnumeration.SNOW_AND_ICE, Locale.getDefault()));
    }
}
