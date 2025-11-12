package no.entur.mummu.services;

import io.swagger.v3.oas.annotations.media.Schema;
import no.entur.mummu.util.MultimodalFilter;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.util.List;

public class StopPlacesRequestParams {

    @Schema(
        description = "Maximum number of stop places to return per page",
        example = "10",
        defaultValue = "10"
    )
    private Integer count;

    @Schema(
        description = "Number of stop places to skip for pagination",
        example = "0",
        defaultValue = "0"
    )
    private Integer skip;

    @Schema(
        description = "Filter by specific stop place IDs",
        example = "[\"NSR:StopPlace:337\", \"NSR:StopPlace:418\"]"
    )
    private List<String> ids;

    @Schema(
        description = "Filter by multimodal hierarchy: 'parent' for parent stop places only, 'child' for child stop places only, 'both' for all",
        example = "both",
        defaultValue = "both",
        allowableValues = {"parent", "child", "both"}
    )
    private MultimodalFilter.MultimodalFilterType multimodal;

    @Schema(
        description = "Filter by one or more transport modes",
        example = "[\"RAIL\", \"BUS\"]"
    )
    private List<VehicleModeEnumeration> transportModes;

    @Schema(
        description = "Filter by one or more stop place types",
        example = "[\"RAIL_STATION\", \"BUS_STATION\"]"
    )
    private List<StopTypeEnumeration> stopPlaceTypes;

    @Schema(
        description = "Filter by topographic place IDs (municipalities, counties)",
        example = "[\"KVE:TopographicPlace:03\"]"
    )
    private List<String> topographicPlaceIds;

    @Schema(
        description = "Filter stop places that contain specific quay IDs",
        example = "[\"NSR:Quay:692\"]"
    )
    private List<String> quayIds;

    public Integer getCount() {
        return count != null ? count : 10;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSkip() {
        return skip != null ? skip : 0;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public MultimodalFilter.MultimodalFilterType getMultimodal() {
        return multimodal != null ? multimodal : MultimodalFilter.MultimodalFilterType.both;
    }

    public void setMultimodal(MultimodalFilter.MultimodalFilterType multimodal) {
        this.multimodal = multimodal;
    }

    public List<VehicleModeEnumeration> getTransportModes() {
        return transportModes;
    }

    public void setTransportModes(List<VehicleModeEnumeration> transportModes) {
        this.transportModes = transportModes;
    }

    public List<StopTypeEnumeration> getStopPlaceTypes() {
        return stopPlaceTypes;
    }

    public void setStopPlaceTypes(List<StopTypeEnumeration> stopPlaceTypes) {
        this.stopPlaceTypes = stopPlaceTypes;
    }

    public List<String> getTopographicPlaceIds() {
        return topographicPlaceIds;
    }

    public void setTopographicPlaceIds(List<String> topographicPlaceIds) {
        this.topographicPlaceIds = topographicPlaceIds;
    }

    public List<String> getQuayIds() {
        return quayIds;
    }

    public void setQuayIds(List<String> quayIds) {
        this.quayIds = quayIds;
    }
}
