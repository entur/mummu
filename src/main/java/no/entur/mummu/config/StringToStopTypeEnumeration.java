package no.entur.mummu.config;

import org.rutebanken.netex.model.StopTypeEnumeration;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class StringToStopTypeEnumeration implements Formatter<StopTypeEnumeration> {
    @Override
    public StopTypeEnumeration parse(String text, Locale locale) throws ParseException {
        try {
            return StopTypeEnumeration.fromValue(text);
        } catch(IllegalArgumentException e) {
            return StopTypeEnumeration.valueOf(text);
        }
    }

    @Override
    public String print(StopTypeEnumeration object, Locale locale) {
        return object.value();
    }
}
