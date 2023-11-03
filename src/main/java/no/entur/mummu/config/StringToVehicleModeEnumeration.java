package no.entur.mummu.config;

import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class StringToVehicleModeEnumeration implements Formatter<VehicleModeEnumeration> {

    @Override
    public VehicleModeEnumeration parse(String text, Locale locale) throws ParseException {
        try {
            return VehicleModeEnumeration.fromValue(text);
        } catch(IllegalArgumentException e) {
            return VehicleModeEnumeration.valueOf(text);
        }
    }

    @Override
    public String print(VehicleModeEnumeration object, Locale locale) {
        return object.value();
    }
}
