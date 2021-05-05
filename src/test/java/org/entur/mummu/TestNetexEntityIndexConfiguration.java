package org.entur.mummu;

import org.entur.netex.index.NetexEntityIndex;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestNetexEntityIndexConfiguration {
    @Bean
    public NetexEntityIndexReadOnlyView netexEntityIndex() {
        var index = new NetexEntityIndex();
        var stopPlace = new StopPlace()
                .withId("NSR:StopPlace:1")
                .withName(new MultilingualString().withValue("Awesome stop"));
        index.stopPlaceById.add(stopPlace);
        return index.readOnlyView();
    }
}
