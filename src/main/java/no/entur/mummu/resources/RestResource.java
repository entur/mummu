package no.entur.mummu.resources;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.xml.bind.JAXBElement;
import no.entur.mummu.services.NetexEntitiesService;
import no.entur.mummu.services.NetexObjectFactory;
import no.entur.mummu.services.StopPlacesRequestParams;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfStopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;


@RestController
@Tag(name = "Stop Place Register Entities", description = "REST API for stop place related NeTEx entities")
public class RestResource {
    private final NetexEntitiesService netexEntitiesService;
    private static final NetexObjectFactory netexObjectFactory = new NetexObjectFactory();


    @Autowired
    public RestResource(
            NetexEntitiesService netexEntitiesService
    ) {
        this.netexEntitiesService = netexEntitiesService;
    }

    @Operation(
            summary = "List groups of stop places",
            description = "Retrieves a paginated list of stop place groups providing logical collections of stop places for organizational purposes. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Groupings"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of stop place groups",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GroupOfStopPlaces.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/groups-of-stop-places", produces = "application/json")
    public List<GroupOfStopPlaces> getGroupOfStopPlaces(
            @Parameter(description = "Maximum number of results to return per page", example = "20")
            @RequestParam(defaultValue = "20") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific group IDs")
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getGroupsOfStopPlaces(count, skip, ids);
    }

    @Hidden
    @GetMapping(value = "/groups-of-stop-places", produces = "application/xml")
    public JAXBElement<GroupsOfStopPlacesInFrame_RelStructure> getJAXBElementGroupOfStopPlaces(
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var groupsOfStopPlaces = netexEntitiesService.getGroupsOfStopPlaces(count, skip, ids);
        return netexObjectFactory.createGroupsOfStopPlaces(groupsOfStopPlaces);
    }

    @Operation(
            summary = "Get group of stop places by ID",
            description = "Retrieves detailed information about a specific stop place group including its name, purpose, and list of member stop places. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Groupings"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stop place group",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupOfStopPlaces.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Group not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/groups-of-stop-places/{id}", produces = {"application/json", "application/xml"})
    public GroupOfStopPlaces getGroupOfStopPlacesById(
            @Parameter(description = "Unique identifier for the stop place group", example = "NSR:GroupOfStopPlaces:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getGroupOfStopPlaces(id);
    }

    @Operation(
            summary = "List stop places",
            description = "Retrieves a paginated list of stop places with optional filtering. Stop places represent physical locations where passengers can board or alight from public transport vehicles. Results can be filtered by transport mode, stop place type, geographic location, and hierarchy. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of stop places. Returns JSON by default, or XML if Accept: application/xml header is specified.",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StopPlace.class)),
                            examples = @ExampleObject(
                                    name = "Stop Places List",
                                    summary = "Example list of stop places",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:StopPlace:337",
                                                "version": "25",
                                                "name": {"value": "Oslo S", "lang": "no"},
                                                "centroid": {
                                                  "location": {"longitude": 10.7522, "latitude": 59.9111}
                                                },
                                                "transportMode": "RAIL",
                                                "stopPlaceType": "RAIL_STATION"
                                              },
                                              {
                                                "id": "NSR:StopPlace:418",
                                                "version": "12",
                                                "name": {"value": "Nationaltheatret", "lang": "no"},
                                                "centroid": {
                                                  "location": {"longitude": 10.7349, "latitude": 59.9149}
                                                },
                                                "transportMode": "METRO",
                                                "stopPlaceType": "METRO_STATION"
                                              }
                                            ]
                                            """
                            )
                    ),
                    @Content(mediaType = "application/xml")
            }
    )
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/stop-places", produces = "application/json")
    public Collection<StopPlace> getStopPlaces(@ParameterObject StopPlacesRequestParams params) {
        return netexEntitiesService.getStopPlaces(params);
    }

    @Hidden
    @GetMapping(value = "/stop-places", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaces(@ParameterObject StopPlacesRequestParams params) {
        var stopPlaces = netexEntitiesService.getStopPlaces(params);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }

    @Operation(
            summary = "Get stop place by ID",
            description = "Retrieves detailed information about a specific stop place by its unique identifier. Returns the latest version including name, location coordinates, transport modes, accessibility features, quays, and associated facilities. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stop place",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}", produces = "application/json")
    public StopPlace getStopPlaceById(
            @Parameter(description = "Unique identifier for the stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getStopPlace(id);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceById(@PathVariable String id) {
        var stopPlace = netexEntitiesService.getStopPlace(id);
        return netexObjectFactory.createStopPlace(stopPlace);
    }

    @Operation(
            summary = "Get scheduled stop points for stop place",
            description = "Retrieves all scheduled stop points associated with a specific stop place. Returns the logical timetable references that map to this physical location. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved scheduled stop points",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScheduledStopPoint.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}/scheduled-stop-points", produces = "application/json")
    public Collection<ScheduledStopPoint> getScheduledStopPointsForStopPlace(
            @Parameter(description = "Unique identifier for the stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getScheduledStopPointsForStopPlaceWithId(id);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}/scheduled-stop-points", produces = "application/xml")
    public JAXBElement<ScheduledStopPointsInFrame_RelStructure> getJAXBElementScheduledStopPointsForStopPlace(@PathVariable String id) {
        var scheduledStopPoints = netexEntitiesService.getScheduledStopPointsForStopPlaceWithId(id);
        return netexObjectFactory.createScheduledStopPoints(scheduledStopPoints);
    }

    @Operation(
            summary = "List stop place versions",
            description = "Retrieves all historical versions of a specific stop place. Useful for tracking changes over time including modifications to location, name, facilities, or operational status. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stop place versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StopPlace.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/json")
    public Collection<StopPlace> getStopPlaceVersions(
            @Parameter(description = "Unique identifier for the stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getStopPlaceVersions(id);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}/versions", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaceVersions(@PathVariable String id) {
        var stopPlaces = netexEntitiesService.getStopPlaceVersions(id);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }

    @Operation(
            summary = "Get specific stop place version",
            description = "Retrieves a specific historical version of a stop place by ID and version number. Provides access to the exact state of the stop place data at a specific point in time. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stop place version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Stop place or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/json")
    public StopPlace getStopPlaceVersion(
            @Parameter(description = "Unique identifier for the stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "25", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getStopPlaceVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceVersion(@PathVariable String id, @PathVariable String version) {
        var stopPlace = netexEntitiesService.getStopPlaceVersion(id, version);
        return netexObjectFactory.createStopPlace(stopPlace);
    }

    @Operation(
            summary = "Get child stop places",
            description = "Retrieves all child stop places for a parent (multimodal) stop place. Parent stop places are hubs that group together multiple related stops, such as different platforms or transport modes at a single location. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved child stop places",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StopPlace.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Parent stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}/children", produces = "application/json")
    public Collection<StopPlace> getStopPlaceChildren(
            @Parameter(description = "Unique identifier for the parent stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getStopPlaceChildren(id);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}/children", produces = "application/xml")
    public JAXBElement<StopPlacesInFrame_RelStructure> getJAXBElementStopPlaceChildren(@PathVariable String id) {
        var stopPlaces = netexEntitiesService.getStopPlaceChildren(id);
        return netexObjectFactory.createStopPlaces(stopPlaces);
    }

    @Operation(
            summary = "Get parkings for stop place",
            description = "Retrieves all parking facilities associated with a specific stop place. Includes park-and-ride facilities, bike parking, and other parking options available at or near the stop place. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Stop Places"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved parking facilities",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/stop-places/{id}/parkings", produces = "application/json")
    public Collection<Parking> getParkingByStopPlaceId(
            @Parameter(description = "Unique identifier for the stop place", example = "NSR:StopPlace:337", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getParkingByStopPlaceId(id);
    }

    @Hidden
    @GetMapping(value = "/stop-places/{id}/parkings", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkingByStopPlaceId(@PathVariable String id) {
        var parkings = netexEntitiesService.getParkingByStopPlaceId(id);
        return netexObjectFactory.createParkings(parkings);
    }

    @Operation(
            summary = "List quays",
            description = "Retrieves a paginated list of quays. Quays represent specific boarding positions within a stop place, such as platforms at a train station or bus stands at a bus terminal. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Quays"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of quays",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Quay.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/quays", produces = "application/json")
    public List<Quay> getQuays(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific quay IDs")
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getQuays(count, skip, ids);
    }

    @Hidden
    @GetMapping(value = "/quays", produces = "application/xml")
    public JAXBElement<Quays_RelStructure> getJAXBElementQuays(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var quays = netexEntitiesService.getQuays(count, skip, ids);
        return netexObjectFactory.createQuays(quays);
    }

    @Operation(
            summary = "Get quay by ID",
            description = "Retrieves detailed information about a specific quay by its unique identifier. Returns location, compass bearing, public code, accessibility features, and equipment. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Quays"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved quay",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Quay.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Quay not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/quays/{id}", produces = "application/json")
    public Quay getQuayById(
            @Parameter(description = "Unique identifier for the quay", example = "NSR:Quay:1234", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getQuayById(id);
    }

    @Hidden
    @GetMapping(value = "/quays/{id}", produces = "application/xml")
    public JAXBElement<Quay> getJAXBElementQuayById(@PathVariable String id) {
        return netexObjectFactory.createQuay(netexEntitiesService.getQuayById(id));
    }

    @Operation(
            summary = "List quay versions",
            description = "Retrieves all historical versions of a specific quay. Tracks changes such as platform number changes, accessibility improvements, or equipment updates over time. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Quays"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved quay versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Quay.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Quay not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/quays/{id}/versions", produces = "application/json")
    public Collection<Quay> getQuayVersions(
            @Parameter(description = "Unique identifier for the quay", example = "NSR:Quay:1234", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getQuayVersions(id);
    }

    @Hidden
    @GetMapping(value = "/quays/{id}/versions", produces = "application/xml")
    public JAXBElement<Quays_RelStructure> getJAXBElementQuayVersions(@PathVariable String id) {
        var quays = netexEntitiesService.getQuayVersions(id);
        return netexObjectFactory.createQuays(quays);
    }

    @Operation(
            summary = "Get specific quay version",
            description = "Retrieves a specific historical version of a quay by ID and version number. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Quays"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved quay version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Quay.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Quay or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/quays/{id}/versions/{version}", produces = "application/json")
    public Quay getQuayVersion(
            @Parameter(description = "Unique identifier for the quay", example = "NSR:Quay:1234", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "5", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getQuayVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/quays/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<Quay> getJAXBElementQuayVersion(@PathVariable String id, @PathVariable String version) {
        return netexObjectFactory.createQuay(netexEntitiesService.getQuayVersion(id, version));
    }

    @Operation(
            summary = "Get stop place for quay",
            description = "Retrieves the parent stop place that contains the specified quay. Useful for finding the complete context and location information for a specific platform. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Quays"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stop place",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Quay or stop place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "quays/{id}/stop-place", produces = "application/json")
    public StopPlace getStopPlaceByQuayId(
            @Parameter(description = "Unique identifier for the quay", example = "NSR:Quay:1234", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getStopPlaceByQuayId(id);
    }

    @Hidden
    @GetMapping(value = "quays/{id}/stop-place", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlaceByQuayId(@PathVariable String id) {
        return netexObjectFactory.createStopPlace(netexEntitiesService.getStopPlaceByQuayId(id));
    }

    @Operation(
            summary = "List parking facilities",
            description = "Retrieves a paginated list of parking facilities including park-and-ride facilities and bike parking associated with public transport stops. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Parking"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of parking facilities",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/parkings", produces = "application/json")
    public List<Parking> getParkings(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific parking IDs")
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getParkings(count, skip, ids);
    }

    @Hidden
    @GetMapping(value = "/parkings", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkings(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var parkings = netexEntitiesService.getParkings(count, skip, ids);
        return netexObjectFactory.createParkings(parkings);
    }

    @Operation(
            summary = "Get parking facility by ID",
            description = "Retrieves detailed information about a specific parking facility including location, capacity, parking types, pricing, and accessibility features. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Parking"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved parking facility",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Parking.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Parking facility not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/parkings/{id}", produces = "application/json")
    public Parking getParkingById(
            @Parameter(description = "Unique identifier for the parking facility", example = "NSR:Parking:5678", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getParkingById(id);
    }

    @Hidden
    @GetMapping(value = "/parkings/{id}", produces = "application/xml")
    public JAXBElement<Parking> getJAXBElementParkingById(@PathVariable String id) {
        return netexObjectFactory.createParking(netexEntitiesService.getParkingById(id));
    }

    @Operation(
            summary = "List parking facility versions",
            description = "Retrieves all historical versions of a specific parking facility. Tracks changes to capacity, pricing, or facility features. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Parking"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved parking facility versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Parking facility not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/parkings/{id}/versions", produces = "application/json")
    public Collection<Parking> getParkingVersions(
            @Parameter(description = "Unique identifier for the parking facility", example = "NSR:Parking:5678", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getParkingVersions(id);
    }

    @Hidden
    @GetMapping(value = "/parkings/{id}/versions", produces = "application/xml")
    public JAXBElement<ParkingsInFrame_RelStructure> getJAXBElementParkingVersions(@PathVariable String id) {
        var parkings = netexEntitiesService.getParkingVersions(id);
        return netexObjectFactory.createParkings(parkings);
    }

    @Operation(
            summary = "Get specific parking facility version",
            description = "Retrieves a specific historical version of a parking facility by ID and version number. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Parking"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved parking facility version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Parking.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Parking facility or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/parkings/{id}/versions/{version}", produces = "application/json")
    public Parking getParkingVersion(
            @Parameter(description = "Unique identifier for the parking facility", example = "NSR:Parking:5678", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "3", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getParkingVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/parkings/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<Parking> getJAXBElementParkingVersion(@PathVariable String id, @PathVariable String version) {
        return netexObjectFactory.createParking(netexEntitiesService.getParkingVersion(id, version));
    }

    @Operation(
            summary = "List topographic places",
            description = "Retrieves a paginated list of topographic places representing administrative and geographic areas such as municipalities, counties, and countries. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Geographic Areas"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of topographic places",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TopographicPlace.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/topographic-places", produces = "application/json")
    public List<TopographicPlace> getTopographicPlaces(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific topographic place IDs")
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getTopographicPlaces(count, skip, ids);
    }

    @Hidden
    @GetMapping(value = "/topographic-places", produces = "application/xml")
    public JAXBElement<TopographicPlacesInFrame_RelStructure> getJAXBElementTopographicPlaces(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var topographicPlaces = netexEntitiesService.getTopographicPlaces(count, skip, ids);
        return netexObjectFactory.createTopographicPlaces(topographicPlaces);
    }

    @Operation(
            summary = "Get topographic place by ID",
            description = "Retrieves detailed information about a specific topographic place including name, type, boundaries, and hierarchical relationships. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Geographic Areas"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved topographic place",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TopographicPlace.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Topographic place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/topographic-places/{id}", produces = "application/json")
    public TopographicPlace getTopographicPlaceById(
            @Parameter(description = "Unique identifier for the topographic place", example = "KVE:TopographicPlace:03", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getTopographicPlaceById(id);
    }

    @Hidden
    @GetMapping(value = "/topographic-places/{id}", produces = "application/xml")
    public JAXBElement<TopographicPlace> getJAXBElementTopographicPlaceById(@PathVariable String id) {
        var topographicPlace = netexEntitiesService.getTopographicPlaceById(id);
        return netexObjectFactory.createTopographicPlace(topographicPlace);
    }

    @Operation(
            summary = "List topographic place versions",
            description = "Retrieves all historical versions of a specific topographic place. Tracks changes to boundaries, names, or administrative classifications. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Geographic Areas"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved topographic place versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TopographicPlace.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Topographic place not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/topographic-places/{id}/versions", produces = "application/json")
    public Collection<TopographicPlace> getTopographicPlaceVersions(
            @Parameter(description = "Unique identifier for the topographic place", example = "KVE:TopographicPlace:03", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getTopographicPlaceVersions(id);
    }

    @Hidden
    @GetMapping(value = "/topographic-places/{id}/versions", produces = "application/xml")
    public JAXBElement<TopographicPlacesInFrame_RelStructure> getJAXBElementTopographicPlaceVersions(@PathVariable String id) {
        var topographicPlaces = netexEntitiesService.getTopographicPlaceVersions(id);
        return netexObjectFactory.createTopographicPlaces(topographicPlaces);
    }

    @Operation(
            summary = "Get specific topographic place version",
            description = "Retrieves a specific historical version of a topographic place by ID and version number. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Geographic Areas"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved topographic place version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TopographicPlace.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Topographic place or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/topographic-places/{id}/versions/{version}", produces = "application/json")
    public TopographicPlace getTopographicPlaceVersion(
            @Parameter(description = "Unique identifier for the topographic place", example = "KVE:TopographicPlace:03", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "2", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getTopographicPlaceVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/topographic-places/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<TopographicPlace> getJAXBElementTopographicPlaceVersion(@PathVariable String id, @PathVariable String version) {
        var topographicPlace = netexEntitiesService.getTopographicPlaceVersion(id, version);
        return netexObjectFactory.createTopographicPlace(topographicPlace);
    }

    @Operation(
            summary = "List tariff zones (deprecated)",
            description = "Retrieves a paginated list of tariff zones. DEPRECATED: Use /fare-zones instead. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"},
            deprecated = true
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tariff zones",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TariffZone.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/tariff-zones", produces = "application/json")
    public List<TariffZone> getTariffZones(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific tariff zone IDs")
            @RequestParam(required = false) List<String> ids,
            @Parameter(description = "Filter by transport authority references")
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        return netexEntitiesService.getTariffZones(count, skip, ids, authorityRefs);
    }

    @Hidden
    @GetMapping(value = "/tariff-zones", produces = "application/xml")
    public JAXBElement<TariffZonesInFrame_RelStructure> getJAXBElementTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        var tariffZones = netexEntitiesService.getTariffZones(count, skip, ids, authorityRefs);
        return netexObjectFactory.createTariffZones(tariffZones);
    }

    @Operation(
            summary = "Get tariff zone by ID (deprecated)",
            description = "Retrieves detailed information about a specific tariff zone. DEPRECATED: Use /fare-zones/{id} instead. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"},
            deprecated = true
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tariff zone",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TariffZone.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Tariff zone not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/tariff-zones/{id}", produces = "application/json")
    public TariffZone getTariffZoneById(
            @Parameter(description = "Unique identifier for the tariff zone", example = "RUT:TariffZone:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getTariffZone(id);
    }

    @Hidden
    @GetMapping(value = "/tariff-zones/{id}", produces = "application/xml")
    public JAXBElement<TariffZone> getJAXBElementTariffZoneById(@PathVariable String id) {
        var tariffZone = netexEntitiesService.getTariffZone(id);
        return netexObjectFactory.createTariffZone(tariffZone);
    }

    @Operation(
            summary = "List tariff zone versions (deprecated)",
            description = "Retrieves all historical versions of a specific tariff zone. DEPRECATED: Use /fare-zones/{id}/versions instead. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"},
            deprecated = true
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tariff zone versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TariffZone.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Tariff zone not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/tariff-zones/{id}/versions", produces = "application/json")
    public Collection<TariffZone> getTariffZoneVersions(
            @Parameter(description = "Unique identifier for the tariff zone", example = "RUT:TariffZone:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getTariffZoneVersions(id);
    }

    @Hidden
    @GetMapping(value = "/tariff-zones/{id}/versions", produces = "application/xml")
    public JAXBElement<TariffZonesInFrame_RelStructure> getJAXBElementTariffZoneVersions(@PathVariable String id) {
        var tariffZones = netexEntitiesService.getTariffZoneVersions(id);
        return netexObjectFactory.createTariffZones(tariffZones);
    }

    @Operation(
            summary = "Get specific tariff zone version (deprecated)",
            description = "Retrieves a specific historical version of a tariff zone. DEPRECATED: Use /fare-zones/{id}/versions/{version} instead. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"},
            deprecated = true
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tariff zone version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TariffZone.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Tariff zone or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/tariff-zones/{id}/versions/{version}", produces = "application/json")
    public TariffZone getTariffZoneVersion(
            @Parameter(description = "Unique identifier for the tariff zone", example = "RUT:TariffZone:1", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "2", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getTariffZoneVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/tariff-zones/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<TariffZone> getJAXBElementTariffZoneVersion(@PathVariable String id, @PathVariable String version) {
        var tariffZone = netexEntitiesService.getTariffZoneVersion(id, version);
        return netexObjectFactory.createTariffZone(tariffZone);
    }

    @Operation(
            summary = "List groups of tariff zones",
            description = "Retrieves a paginated list of tariff zone groups organizing fare zones into logical collections for ticketing and pricing purposes. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Groupings"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tariff zone groups",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GroupOfTariffZones.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/json")
    public List<GroupOfTariffZones> getGroupsOfTariffZones(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific tariff zone group IDs")
            @RequestParam(required = false) List<String> ids
    ) {
        return netexEntitiesService.getGroupsOfTariffZones(count, skip, ids);
    }

    @Hidden
    @GetMapping(value = "/groups-of-tariff-zones", produces = "application/xml")
    public JAXBElement<GroupsOfTariffZonesInFrame_RelStructure> getJAXBElementGroupsOfTariffZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids
    ) {
        var groupsOfTariffZones = netexEntitiesService.getGroupsOfTariffZones(count, skip, ids);
        return netexObjectFactory.createGroupsOfTariffZones(groupsOfTariffZones);
    }

    @Operation(
            summary = "Get group of tariff zones by ID",
            description = "Retrieves detailed information about a specific tariff zone group including member zones and pricing relationships. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Groupings"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tariff zone group",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupOfTariffZones.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Tariff zone group not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/json")
    public GroupOfTariffZones getGroupOfTariffZonesById(
            @Parameter(description = "Unique identifier for the tariff zone group", example = "RUT:GroupOfTariffZones:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getGroupOfTariffZonesById(id);
    }

    @Hidden
    @GetMapping(value = "/groups-of-tariff-zones/{id}", produces = "application/xml")
    public JAXBElement<GroupOfTariffZones> getJAXBElementGroupOfTariffZonesById(@PathVariable String id) {
        var groupOfTariffZones = netexEntitiesService.getGroupOfTariffZonesById(id);
        return netexObjectFactory.createGroupOfTariffZones(groupOfTariffZones);
    }

    @Operation(
            summary = "List fare zones",
            description = "Retrieves a paginated list of fare zones. Fare zones define geographic areas used for fare calculation and ticket pricing in public transport systems. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of fare zones",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FareZone.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/fare-zones", produces = "application/json")
    public List<FareZone> getFareZones(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip,
            @Parameter(description = "Filter by specific fare zone IDs")
            @RequestParam(required = false) List<String> ids,
            @Parameter(description = "Filter by transport authority references")
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        return netexEntitiesService.getFareZones(count, skip, ids, authorityRefs);
    }

    @Hidden
    @GetMapping(value = "/fare-zones", produces = "application/xml")
    public JAXBElement<FareZonesInFrame_RelStructure> getJAXBElementFareZones(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) List<String> authorityRefs
    ) {
        var fareZones = netexEntitiesService.getFareZones(count, skip, ids, authorityRefs);
        return netexObjectFactory.createFareZones(fareZones);
    }

    @Operation(
            summary = "Get fare zone by ID",
            description = "Retrieves detailed information about a specific fare zone including boundaries, member stop places, and authority information. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved fare zone",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = FareZone.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Fare zone not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/fare-zones/{id}", produces = "application/json")
    public FareZone getFareZoneById(
            @Parameter(description = "Unique identifier for the fare zone", example = "RUT:FareZone:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getFareZone(id);
    }

    @Hidden
    @GetMapping(value = "/fare-zones/{id}", produces = "application/xml")
    public JAXBElement<FareZone> getJAXBElementFareZoneById(@PathVariable String id) {
        return netexObjectFactory.createFareZone(netexEntitiesService.getFareZone(id));
    }

    @Operation(
            summary = "List fare zone versions",
            description = "Retrieves all historical versions of a specific fare zone. Tracks changes to zone boundaries, pricing structures, or member stop places. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved fare zone versions",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FareZone.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Fare zone not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/fare-zones/{id}/versions", produces = "application/json")
    public Collection<FareZone> getFareZoneVersions(
            @Parameter(description = "Unique identifier for the fare zone", example = "RUT:FareZone:1", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getFareZoneVersions(id);
    }

    @Hidden
    @GetMapping(value = "/fare-zones/{id}/versions", produces = "application/xml")
    public JAXBElement<FareZonesInFrame_RelStructure> getJAXBElementFareZoneVersions(@PathVariable String id) {
        var fareZones = netexEntitiesService.getFareZoneVersions(id);
        return netexObjectFactory.createFareZones(fareZones);
    }

    @Operation(
            summary = "Get specific fare zone version",
            description = "Retrieves a specific historical version of a fare zone by ID and version number. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Fare Zones"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved fare zone version",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = FareZone.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Fare zone or version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/fare-zones/{id}/versions/{version}", produces = "application/json")
    public FareZone getFareZoneVersion(
            @Parameter(description = "Unique identifier for the fare zone", example = "RUT:FareZone:1", required = true)
            @PathVariable String id,
            @Parameter(description = "Version number", example = "2", required = true)
            @PathVariable String version
    ) {
        return netexEntitiesService.getFareZoneVersion(id, version);
    }

    @Hidden
    @GetMapping(value = "/fare-zones/{id}/versions/{version}", produces = "application/xml")
    public JAXBElement<FareZone> getJAXBElementFareZoneVersion(@PathVariable String id, @PathVariable String version) {
        var fareZone = netexEntitiesService.getFareZoneVersion(id, version);
        return netexObjectFactory.createFareZone(fareZone);
    }

    @Operation(
            summary = "List scheduled stop points",
            description = "Retrieves a paginated list of scheduled stop points. These are logical references used in route and timetable data that map to physical stop places. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Scheduled Stop Points"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of scheduled stop points",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScheduledStopPoint.class))),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/scheduled-stop-points", produces = "application/json")
    public List<ScheduledStopPoint> getScheduledStopPoints(
            @Parameter(description = "Maximum number of results to return per page", example = "10")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Number of results to skip for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer skip
    ) {
        return netexEntitiesService.getScheduledStopPoints(count, skip);
    }

    @Hidden
    @GetMapping(value = "/scheduled-stop-points", produces = "application/xml")
    public JAXBElement<ScheduledStopPointsInFrame_RelStructure> getJAXBElementScheduledStopPoints(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer skip
    ) {
        var scheduledStopPoints = netexEntitiesService.getScheduledStopPoints(count, skip);
        return netexObjectFactory.createScheduledStopPoints(scheduledStopPoints);
    }

    @Operation(
            summary = "Get scheduled stop point by ID",
            description = "Retrieves detailed information about a specific scheduled stop point by its unique identifier. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Scheduled Stop Points"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved scheduled stop point",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ScheduledStopPoint.class)),
                    @Content(mediaType = "application/xml")
            })
    @ApiResponse(responseCode = "404", description = "Scheduled stop point not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/scheduled-stop-points/{id}", produces = "application/json")
    public ScheduledStopPoint getScheduledStopPointById(
            @Parameter(description = "Unique identifier for the scheduled stop point", example = "RUT:ScheduledStopPoint:03011605", required = true)
            @PathVariable String id
    ) {
        return netexEntitiesService.getScheduledStopPoint(id);
    }

    @Hidden
    @GetMapping(value = "/scheduled-stop-points/{id}", produces = "application/xml")
    public JAXBElement<ScheduledStopPoint> getJAXBElementScheduledStopPointById(@PathVariable String id) {
        return netexObjectFactory.createScheduledStopPoint(netexEntitiesService.getScheduledStopPoint(id));
    }

    @Operation(
            summary = "Get stop place for scheduled stop point",
            description = "Retrieves the physical stop place associated with a scheduled stop point. Maps the logical reference used in timetables to the actual physical infrastructure where passengers board vehicles. Supports both JSON (default) and XML formats via Accept header.",
            tags = {"Scheduled Stop Points"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved stop place",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Oslo S Example",
                                    summary = "Stop place retrieved from scheduled stop point",
                                    value = """
                                            {
                                              "id": "NSR:StopPlace:337",
                                              "version": "25",
                                              "name": {"value": "Oslo S", "lang": "no"},
                                              "centroid": {
                                                "location": {"longitude": 10.7522, "latitude": 59.9111}
                                              },
                                              "transportMode": "RAIL",
                                              "stopPlaceType": "RAIL_STATION"
                                            }
                                            """
                            )
                    ),
                    @Content(mediaType = "application/xml")
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Scheduled stop point or associated stop place not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "errorCode": "RESOURCE_NOT_FOUND",
                                      "message": "Stop place for scheduled stop point 'RUT:ScheduledStopPoint:99999' not found"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @GetMapping(value = "/scheduled-stop-points/{id}/stop-place", produces = "application/json")
    public StopPlace getStopPlaceByScheduledStopPointId(
            @Parameter(
                    description = "Unique identifier for the scheduled stop point in NeTEx format",
                    example = "RUT:ScheduledStopPoint:03011605",
                    required = true
            )
            @PathVariable String id
    ) {
        return netexEntitiesService.getStopPlaceByScheduledStopPointId(id);
    }

    @Hidden
    @GetMapping(value = "/scheduled-stop-points/{id}/stop-place", produces = "application/xml")
    public JAXBElement<StopPlace> getJAXBElementStopPlacesByScheduledStopPointId(@PathVariable String id) {
        return netexObjectFactory.createStopPlace(netexEntitiesService.getStopPlaceByScheduledStopPointId(id));
    }
}
