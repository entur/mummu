package no.entur.mummu.services;

import io.swagger.v3.oas.annotations.media.Schema;
import no.entur.mummu.util.MultimodalFilter;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;

import java.util.List;

public class StopPlacesRequestParams {

    @Schema(defaultValue = "10")
    private Integer count;

    @Schema(defaultValue = "0")
    private Integer skip;

    private List<String> ids;

    @Schema(defaultValue = "both")
    private MultimodalFilter.MultimodalFilterType multimodal;

    private List<VehicleModeEnumeration> transportModes;

    private List<StopTypeEnumeration> stopPlaceTypes;

    private List<String> topographicPlaceIds;

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
