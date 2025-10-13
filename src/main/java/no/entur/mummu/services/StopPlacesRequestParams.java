package no.entur.mummu.services;

import no.entur.mummu.util.MultimodalFilter;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.util.List;

public record StopPlacesRequestParams(
        Integer count,
        Integer skip,
        List<String> ids,
        MultimodalFilter.MultimodalFilterType multimodal,
        List<VehicleModeEnumeration> transportModes,
        List<StopTypeEnumeration> stopPlaceTypes,
        List<String> topographicPlaceIds,
        List<String> quayIds
) {
    public StopPlacesRequestParams {
        // Set defaults for null values
        if (count == null) {
            count = 10;
        }
        if (skip == null) {
            skip = 0;
        }
        if (multimodal == null) {
            multimodal = MultimodalFilter.MultimodalFilterType.both;
        }
    }
}
