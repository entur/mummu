package no.entur.mummu.resources;

import io.swagger.v3.oas.annotations.Parameter;
import no.entur.mummu.util.MultimodalFilter;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.util.List;

public record StopPlacesRequestParams(
        @Parameter(description = "Maximum number of results to return", example = "10")
        Integer count,

        @Parameter(description = "Number of results to skip", example = "0")
        Integer skip,

        @Parameter(description = "Filter by specific IDs")
        List<String> ids,

        @Parameter(description = "Filter by multimodal type", example = "both")
        MultimodalFilter.MultimodalFilterType multimodal,

        @Parameter(description = "Filter by transport modes")
        List<VehicleModeEnumeration> transportModes,

        @Parameter(description = "Filter by stop place types")
        List<StopTypeEnumeration> stopPlaceTypes,

        @Parameter(description = "Filter by topographic place IDs")
        List<String> topographicPlaceIds,

        @Parameter(description = "Filter by quay IDs")
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
