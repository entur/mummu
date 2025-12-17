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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GroupOfStopPlaces.class)),
                            examples = @ExampleObject(
                                    name = "Groups of Stop Places List",
                                    summary = "Example list of stop place groups",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:GroupOfStopPlaces:1",
                                                "version": "13",
                                                "name": {"value": "Oslo", "lang": "nor"},
                                                "centroid": {
                                                  "location": {"longitude": 10.748128, "latitude": 59.911076}
                                                },
                                                "members": {
                                                  "stopPlaceRef": [
                                                    {"ref": "NSR:StopPlace:58366"},
                                                    {"ref": "NSR:StopPlace:59872"}
                                                  ]
                                                }
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = GroupsOfStopPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Groups of Stop Places XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <groupsOfStopPlaces xmlns="http://www.netex.org.uk/netex">
                                              <GroupOfStopPlaces id="NSR:GroupOfStopPlaces:1" version="13">
                                                <Name lang="nor">Oslo</Name>
                                                <Centroid>
                                                  <Location>
                                                    <Longitude>10.748128</Longitude>
                                                    <Latitude>59.911076</Latitude>
                                                  </Location>
                                                </Centroid>
                                                <members>
                                                  <StopPlaceRef ref="NSR:StopPlace:58366"/>
                                                  <StopPlaceRef ref="NSR:StopPlace:59872"/>
                                                </members>
                                              </GroupOfStopPlaces>
                                            </groupsOfStopPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupOfStopPlaces.class),
                            examples = @ExampleObject(
                                    name = "Group of Stop Places",
                                    summary = "Example stop place group",
                                    value = """
                                            {
                                              "id": "NSR:GroupOfStopPlaces:1",
                                              "version": "13",
                                              "name": {"value": "Oslo", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 10.748128, "latitude": 59.911076}
                                              },
                                              "members": {
                                                "stopPlaceRef": [
                                                  {"ref": "NSR:StopPlace:58366"},
                                                  {"ref": "NSR:StopPlace:59872"},
                                                  {"ref": "NSR:StopPlace:58293"},
                                                  {"ref": "NSR:StopPlace:58382"}
                                                ]
                                              },
                                              "purposeOfGroupingRef": {"ref": "NSR:PurposeOfGrouping:1"}
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = GroupOfStopPlaces.class),
                            examples = @ExampleObject(
                                    name = "Group of Stop Places XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <GroupOfStopPlaces xmlns="http://www.netex.org.uk/netex" id="NSR:GroupOfStopPlaces:1" version="13">
                                              <Name lang="nor">Oslo</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.748128</Longitude>
                                                  <Latitude>59.911076</Latitude>
                                                </Location>
                                              </Centroid>
                                              <members>
                                                <StopPlaceRef ref="NSR:StopPlace:58366"/>
                                                <StopPlaceRef ref="NSR:StopPlace:59872"/>
                                              </members>
                                              <PurposeOfGroupingRef ref="NSR:PurposeOfGrouping:1"/>
                                            </GroupOfStopPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Stop Places XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                                              <StopPlace id="NSR:StopPlace:337" version="25">
                                                <Name lang="no">Oslo S</Name>
                                                <Centroid>
                                                  <Location>
                                                    <Longitude>10.7522</Longitude>
                                                    <Latitude>59.9111</Latitude>
                                                  </Location>
                                                </Centroid>
                                                <TransportMode>rail</TransportMode>
                                                <StopPlaceType>railStation</StopPlaceType>
                                              </StopPlace>
                                            </stopPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place",
                                    summary = "Example stop place",
                                    value = """
                                            {
                                              "id": "NSR:StopPlace:337",
                                              "version": "25",
                                              "name": {"value": "Oslo S", "lang": "no"},
                                              "centroid": {
                                                "location": {"longitude": 10.753276, "latitude": 59.910925}
                                              },
                                              "transportMode": "RAIL",
                                              "stopPlaceType": "RAIL_STATION",
                                              "weighting": "PREFERRED_INTERCHANGE",
                                              "quays": {
                                                "quayRefOrQuay": [
                                                  {"id": "NSR:Quay:1", "publicCode": "1"},
                                                  {"id": "NSR:Quay:2", "publicCode": "2"}
                                                ]
                                              }
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <StopPlace xmlns="http://www.netex.org.uk/netex" id="NSR:StopPlace:337" version="25">
                                              <Name lang="no">Oslo S</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.753276</Longitude>
                                                  <Latitude>59.910925</Latitude>
                                                </Location>
                                              </Centroid>
                                              <TransportMode>rail</TransportMode>
                                              <StopPlaceType>railStation</StopPlaceType>
                                              <Weighting>preferredInterchange</Weighting>
                                            </StopPlace>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScheduledStopPoint.class)),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Points",
                                    summary = "Example list of scheduled stop points",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:ScheduledStopPoint:Q1",
                                                "version": "36",
                                                "name": {"value": "Oslo S Trelastgata"}
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ScheduledStopPointsInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Points XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <scheduledStopPoints xmlns="http://www.netex.org.uk/netex">
                                              <ScheduledStopPoint id="NSR:ScheduledStopPoint:Q1" version="36">
                                                <Name>Oslo S Trelastgata</Name>
                                              </ScheduledStopPoint>
                                            </scheduledStopPoints>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StopPlace.class)),
                            examples = @ExampleObject(
                                    name = "Stop Place Versions",
                                    summary = "Example list of stop place versions",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:StopPlace:337",
                                                "version": "25",
                                                "name": {"value": "Oslo S", "lang": "nor"},
                                                "stopPlaceType": "RAIL_STATION",
                                                "transportMode": "RAIL"
                                              },
                                              {
                                                "id": "NSR:StopPlace:337",
                                                "version": "24",
                                                "name": {"value": "Oslo S", "lang": "nor"},
                                                "stopPlaceType": "RAIL_STATION",
                                                "transportMode": "RAIL"
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Stop Place Versions XML",
                                    summary = "Example list of stop place versions in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                                              <StopPlace id="NSR:StopPlace:337" version="25">
                                                <Name lang="nor">Oslo S</Name>
                                                <StopPlaceType>railStation</StopPlaceType>
                                                <TransportMode>rail</TransportMode>
                                              </StopPlace>
                                              <StopPlace id="NSR:StopPlace:337" version="24">
                                                <Name lang="nor">Oslo S</Name>
                                                <StopPlaceType>railStation</StopPlaceType>
                                                <TransportMode>rail</TransportMode>
                                              </StopPlace>
                                            </stopPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place Version",
                                    summary = "Example specific stop place version",
                                    value = """
                                            {
                                              "id": "NSR:StopPlace:337",
                                              "version": "24",
                                              "name": {"value": "Oslo S", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 10.752245, "latitude": 59.910624}
                                              },
                                              "stopPlaceType": "RAIL_STATION",
                                              "transportMode": "RAIL"
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place Version XML",
                                    summary = "Example specific stop place version in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <StopPlace xmlns="http://www.netex.org.uk/netex" id="NSR:StopPlace:337" version="24">
                                              <Name lang="nor">Oslo S</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.752245</Longitude>
                                                  <Latitude>59.910624</Latitude>
                                                </Location>
                                              </Centroid>
                                              <StopPlaceType>railStation</StopPlaceType>
                                              <TransportMode>rail</TransportMode>
                                            </StopPlace>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StopPlace.class)),
                            examples = @ExampleObject(
                                    name = "Child Stop Places",
                                    summary = "Example list of child stop places",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:StopPlace:58366",
                                                "version": "3",
                                                "name": {"value": "Oslo S", "lang": "nor"},
                                                "stopPlaceType": "RAIL_STATION",
                                                "transportMode": "RAIL",
                                                "parentSiteRef": {"ref": "NSR:StopPlace:337"}
                                              },
                                              {
                                                "id": "NSR:StopPlace:58195",
                                                "version": "2",
                                                "name": {"value": "Oslo S", "lang": "nor"},
                                                "stopPlaceType": "BUS_STATION",
                                                "transportMode": "BUS",
                                                "parentSiteRef": {"ref": "NSR:StopPlace:337"}
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Child Stop Places XML",
                                    summary = "Example list of child stop places in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                                              <StopPlace id="NSR:StopPlace:58366" version="3">
                                                <Name lang="nor">Oslo S</Name>
                                                <ParentSiteRef ref="NSR:StopPlace:337"/>
                                                <StopPlaceType>railStation</StopPlaceType>
                                                <TransportMode>rail</TransportMode>
                                              </StopPlace>
                                              <StopPlace id="NSR:StopPlace:58195" version="2">
                                                <Name lang="nor">Oslo S</Name>
                                                <ParentSiteRef ref="NSR:StopPlace:337"/>
                                                <StopPlaceType>busStation</StopPlaceType>
                                                <TransportMode>bus</TransportMode>
                                              </StopPlace>
                                            </stopPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class)),
                            examples = @ExampleObject(
                                    name = "Stop Place Parkings",
                                    summary = "Example list of parking facilities for a stop place",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:Parking:123",
                                                "version": "2",
                                                "name": {"value": "Oslo S - Bike Parking", "lang": "nor"},
                                                "parentSiteRef": {"ref": "NSR:StopPlace:337"},
                                                "totalCapacity": 200,
                                                "parkingVehicleTypes": ["PEDAL_CYCLE"]
                                              },
                                              {
                                                "id": "NSR:Parking:124",
                                                "version": "1",
                                                "name": {"value": "Oslo S - Park and Ride", "lang": "nor"},
                                                "parentSiteRef": {"ref": "NSR:StopPlace:337"},
                                                "totalCapacity": 50,
                                                "parkingVehicleTypes": ["CAR"]
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ParkingsInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Stop Place Parkings XML",
                                    summary = "Example list of parking facilities in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <parkings xmlns="http://www.netex.org.uk/netex">
                                              <Parking id="NSR:Parking:123" version="2">
                                                <Name lang="nor">Oslo S - Bike Parking</Name>
                                                <ParentSiteRef ref="NSR:StopPlace:337"/>
                                                <TotalCapacity>200</TotalCapacity>
                                                <ParkingVehicleTypes>pedalCycle</ParkingVehicleTypes>
                                              </Parking>
                                              <Parking id="NSR:Parking:124" version="1">
                                                <Name lang="nor">Oslo S - Park and Ride</Name>
                                                <ParentSiteRef ref="NSR:StopPlace:337"/>
                                                <TotalCapacity>50</TotalCapacity>
                                                <ParkingVehicleTypes>car</ParkingVehicleTypes>
                                              </Parking>
                                            </parkings>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Quay.class)),
                            examples = @ExampleObject(
                                    name = "Quays List",
                                    summary = "Example list of quays",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:Quay:1",
                                                "version": "36",
                                                "centroid": {
                                                  "location": {"longitude": 10.75525, "latitude": 59.909548}
                                                },
                                                "privateCode": {"value": "30"},
                                                "accessibilityAssessment": {
                                                  "limitations": {
                                                    "accessibilityLimitation": {
                                                      "wheelchairAccess": "TRUE",
                                                      "stepFreeAccess": "TRUE"
                                                    }
                                                  }
                                                }
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Quays_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Quays XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <quays xmlns="http://www.netex.org.uk/netex">
                                              <Quay id="NSR:Quay:1" version="36">
                                                <Centroid>
                                                  <Location>
                                                    <Longitude>10.75525</Longitude>
                                                    <Latitude>59.909548</Latitude>
                                                  </Location>
                                                </Centroid>
                                                <PrivateCode>30</PrivateCode>
                                              </Quay>
                                            </quays>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Quay.class),
                            examples = @ExampleObject(
                                    name = "Quay",
                                    summary = "Example quay",
                                    value = """
                                            {
                                              "id": "NSR:Quay:1",
                                              "version": "36",
                                              "centroid": {
                                                "location": {"longitude": 10.75525, "latitude": 59.909548}
                                              },
                                              "privateCode": {"value": "30"},
                                              "accessibilityAssessment": {
                                                "limitations": {
                                                  "accessibilityLimitation": {
                                                    "wheelchairAccess": "TRUE",
                                                    "stepFreeAccess": "TRUE"
                                                  }
                                                }
                                              },
                                              "placeEquipments": {
                                                "installedEquipmentRefOrInstalledEquipment": [
                                                  {"type": "ShelterEquipment", "value": {"enclosed": false, "seats": 1}}
                                                ]
                                              }
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Quay.class),
                            examples = @ExampleObject(
                                    name = "Quay XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <Quay xmlns="http://www.netex.org.uk/netex" id="NSR:Quay:1" version="36">
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.75525</Longitude>
                                                  <Latitude>59.909548</Latitude>
                                                </Location>
                                              </Centroid>
                                              <PrivateCode>30</PrivateCode>
                                              <AccessibilityAssessment>
                                                <MobilityImpairedAccess>unknown</MobilityImpairedAccess>
                                              </AccessibilityAssessment>
                                            </Quay>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Quay.class)),
                            examples = @ExampleObject(
                                    name = "Quay Versions",
                                    summary = "Example list of quay versions",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:Quay:7203",
                                                "version": "10",
                                                "name": {"value": "Platform 1", "lang": "nor"},
                                                "publicCode": "1"
                                              },
                                              {
                                                "id": "NSR:Quay:7203",
                                                "version": "9",
                                                "name": {"value": "Platform 1", "lang": "nor"},
                                                "publicCode": "1"
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Quays_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Quay Versions XML",
                                    summary = "Example list of quay versions in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <quays xmlns="http://www.netex.org.uk/netex">
                                              <Quay id="NSR:Quay:7203" version="10">
                                                <Name lang="nor">Platform 1</Name>
                                                <PublicCode>1</PublicCode>
                                              </Quay>
                                              <Quay id="NSR:Quay:7203" version="9">
                                                <Name lang="nor">Platform 1</Name>
                                                <PublicCode>1</PublicCode>
                                              </Quay>
                                            </quays>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Quay.class),
                            examples = @ExampleObject(
                                    name = "Quay Version",
                                    summary = "Example specific quay version",
                                    value = """
                                            {
                                              "id": "NSR:Quay:7203",
                                              "version": "9",
                                              "name": {"value": "Platform 1", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 10.752245, "latitude": 59.910624}
                                              },
                                              "publicCode": "1"
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Quay.class),
                            examples = @ExampleObject(
                                    name = "Quay Version XML",
                                    summary = "Example specific quay version in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <Quay xmlns="http://www.netex.org.uk/netex" id="NSR:Quay:7203" version="9">
                                              <Name lang="nor">Platform 1</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.752245</Longitude>
                                                  <Latitude>59.910624</Latitude>
                                                </Location>
                                              </Centroid>
                                              <PublicCode>1</PublicCode>
                                            </Quay>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place for Quay",
                                    summary = "Example stop place containing the quay",
                                    value = """
                                            {
                                              "id": "NSR:StopPlace:337",
                                              "version": "25",
                                              "name": {"value": "Oslo S", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 10.752245, "latitude": 59.910624}
                                              },
                                              "stopPlaceType": "RAIL_STATION",
                                              "transportMode": "RAIL"
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Stop Place for Quay XML",
                                    summary = "Example stop place in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <StopPlace xmlns="http://www.netex.org.uk/netex" id="NSR:StopPlace:337" version="25">
                                              <Name lang="nor">Oslo S</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.752245</Longitude>
                                                  <Latitude>59.910624</Latitude>
                                                </Location>
                                              </Centroid>
                                              <StopPlaceType>railStation</StopPlaceType>
                                              <TransportMode>rail</TransportMode>
                                            </StopPlace>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class)),
                            examples = @ExampleObject(
                                    name = "Parkings List",
                                    summary = "Example list of parking facilities",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:Parking:2",
                                                "version": "3",
                                                "name": {"value": "Kvål", "lang": "nor"},
                                                "centroid": {
                                                  "location": {"longitude": 10.279719, "latitude": 63.233515}
                                                },
                                                "parentSiteRef": {"ref": "NSR:StopPlace:369"},
                                                "totalCapacity": 10,
                                                "parkingVehicleTypes": ["CAR"]
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ParkingsInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Parkings XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <parkings xmlns="http://www.netex.org.uk/netex">
                                              <Parking id="NSR:Parking:2" version="3">
                                                <Name lang="nor">Kvål</Name>
                                                <Centroid>
                                                  <Location>
                                                    <Longitude>10.279719</Longitude>
                                                    <Latitude>63.233515</Latitude>
                                                  </Location>
                                                </Centroid>
                                                <ParentSiteRef ref="NSR:StopPlace:369"/>
                                                <TotalCapacity>10</TotalCapacity>
                                                <ParkingVehicleTypes>car</ParkingVehicleTypes>
                                              </Parking>
                                            </parkings>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Parking.class),
                            examples = @ExampleObject(
                                    name = "Parking",
                                    summary = "Example parking facility",
                                    value = """
                                            {
                                              "id": "NSR:Parking:5",
                                              "version": "5",
                                              "name": {"value": "HiNT / Røstad", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 11.31878, "latitude": 63.752917}
                                              },
                                              "parentSiteRef": {"ref": "NSR:StopPlace:54"},
                                              "totalCapacity": 12,
                                              "parkingVehicleTypes": ["PEDAL_CYCLE"],
                                              "covered": "OUTDOORS"
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Parking.class),
                            examples = @ExampleObject(
                                    name = "Parking XML",
                                    summary = "Example parking facility in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <Parking xmlns="http://www.netex.org.uk/netex" id="NSR:Parking:5" version="5">
                                              <Name lang="nor">HiNT / Røstad</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>11.31878</Longitude>
                                                  <Latitude>63.752917</Latitude>
                                                </Location>
                                              </Centroid>
                                              <ParentSiteRef ref="NSR:StopPlace:54"/>
                                              <TotalCapacity>12</TotalCapacity>
                                              <ParkingVehicleTypes>pedalCycle</ParkingVehicleTypes>
                                              <Covered>outdoors</Covered>
                                            </Parking>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Parking.class)),
                            examples = @ExampleObject(
                                    name = "Parking Versions",
                                    summary = "Example list of parking facility versions",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:Parking:5",
                                                "version": "5",
                                                "name": {"value": "HiNT / Røstad", "lang": "nor"},
                                                "totalCapacity": 12,
                                                "parkingVehicleTypes": ["PEDAL_CYCLE"]
                                              },
                                              {
                                                "id": "NSR:Parking:5",
                                                "version": "4",
                                                "name": {"value": "HiNT / Røstad", "lang": "nor"},
                                                "totalCapacity": 10,
                                                "parkingVehicleTypes": ["PEDAL_CYCLE"]
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ParkingsInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Parking Versions XML",
                                    summary = "Example list of parking facility versions in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <parkings xmlns="http://www.netex.org.uk/netex">
                                              <Parking id="NSR:Parking:5" version="5">
                                                <Name lang="nor">HiNT / Røstad</Name>
                                                <TotalCapacity>12</TotalCapacity>
                                                <ParkingVehicleTypes>pedalCycle</ParkingVehicleTypes>
                                              </Parking>
                                              <Parking id="NSR:Parking:5" version="4">
                                                <Name lang="nor">HiNT / Røstad</Name>
                                                <TotalCapacity>10</TotalCapacity>
                                                <ParkingVehicleTypes>pedalCycle</ParkingVehicleTypes>
                                              </Parking>
                                            </parkings>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Parking.class),
                            examples = @ExampleObject(
                                    name = "Parking Version",
                                    summary = "Example specific parking facility version",
                                    value = """
                                            {
                                              "id": "NSR:Parking:5",
                                              "version": "4",
                                              "name": {"value": "HiNT / Røstad", "lang": "nor"},
                                              "centroid": {
                                                "location": {"longitude": 11.31878, "latitude": 63.752917}
                                              },
                                              "parentSiteRef": {"ref": "NSR:StopPlace:54"},
                                              "totalCapacity": 10,
                                              "parkingVehicleTypes": ["PEDAL_CYCLE"]
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Parking.class),
                            examples = @ExampleObject(
                                    name = "Parking Version XML",
                                    summary = "Example specific parking facility version in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <Parking xmlns="http://www.netex.org.uk/netex" id="NSR:Parking:5" version="4">
                                              <Name lang="nor">HiNT / Røstad</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>11.31878</Longitude>
                                                  <Latitude>63.752917</Latitude>
                                                </Location>
                                              </Centroid>
                                              <ParentSiteRef ref="NSR:StopPlace:54"/>
                                              <TotalCapacity>10</TotalCapacity>
                                              <ParkingVehicleTypes>pedalCycle</ParkingVehicleTypes>
                                            </Parking>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TopographicPlace.class)),
                            examples = @ExampleObject(
                                    name = "Topographic Places List",
                                    summary = "Example list of topographic places",
                                    value = """
                                            [
                                              {
                                                "id": "KVE:TopographicPlace:03",
                                                "version": "1",
                                                "descriptor": {
                                                  "name": {"value": "Oslo", "lang": "nor"}
                                                },
                                                "isoCode": "NO-03",
                                                "countryRef": {"ref": "NO"}
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TopographicPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Topographic Places XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <topographicPlaces xmlns="http://www.netex.org.uk/netex">
                                              <TopographicPlace id="KVE:TopographicPlace:03" version="1">
                                                <Descriptor>
                                                  <Name lang="nor">Oslo</Name>
                                                </Descriptor>
                                                <IsoCode>NO-03</IsoCode>
                                                <CountryRef ref="NO"/>
                                              </TopographicPlace>
                                            </topographicPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TopographicPlace.class),
                            examples = @ExampleObject(
                                    name = "Topographic Place",
                                    summary = "Example topographic place (Oslo county)",
                                    value = """
                                            {
                                              "id": "KVE:TopographicPlace:03",
                                              "version": "1",
                                              "descriptor": {
                                                "name": {"value": "Oslo", "lang": "nor"}
                                              },
                                              "isoCode": "NO-03",
                                              "topographicPlaceType": "COUNTY",
                                              "countryRef": {"ref": "NO"}
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TopographicPlace.class),
                            examples = @ExampleObject(
                                    name = "Topographic Place XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <TopographicPlace xmlns="http://www.netex.org.uk/netex" id="KVE:TopographicPlace:03" version="1">
                                              <Descriptor>
                                                <Name lang="nor">Oslo</Name>
                                              </Descriptor>
                                              <IsoCode>NO-03</IsoCode>
                                              <TopographicPlaceType>county</TopographicPlaceType>
                                              <CountryRef ref="NO"/>
                                            </TopographicPlace>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TopographicPlace.class)),
                            examples = @ExampleObject(
                                    name = "Topographic Place Versions",
                                    summary = "Example list of topographic place versions",
                                    value = """
                                            [
                                              {
                                                "id": "KVE:TopographicPlace:03",
                                                "version": "2",
                                                "name": {"value": "Oslo", "lang": "nor"},
                                                "topographicPlaceType": "COUNTY"
                                              },
                                              {
                                                "id": "KVE:TopographicPlace:03",
                                                "version": "1",
                                                "name": {"value": "Oslo", "lang": "nor"},
                                                "topographicPlaceType": "COUNTY"
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TopographicPlacesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Topographic Place Versions XML",
                                    summary = "Example list of topographic place versions in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <topographicPlaces xmlns="http://www.netex.org.uk/netex">
                                              <TopographicPlace id="KVE:TopographicPlace:03" version="2">
                                                <Name lang="nor">Oslo</Name>
                                                <TopographicPlaceType>county</TopographicPlaceType>
                                              </TopographicPlace>
                                              <TopographicPlace id="KVE:TopographicPlace:03" version="1">
                                                <Name lang="nor">Oslo</Name>
                                                <TopographicPlaceType>county</TopographicPlaceType>
                                              </TopographicPlace>
                                            </topographicPlaces>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TopographicPlace.class),
                            examples = @ExampleObject(
                                    name = "Topographic Place Version",
                                    summary = "Example specific topographic place version",
                                    value = """
                                            {
                                              "id": "KVE:TopographicPlace:03",
                                              "version": "1",
                                              "name": {"value": "Oslo", "lang": "nor"},
                                              "topographicPlaceType": "COUNTY",
                                              "countryRef": {"ref": "NO"}
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TopographicPlace.class),
                            examples = @ExampleObject(
                                    name = "Topographic Place Version XML",
                                    summary = "Example specific topographic place version in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <TopographicPlace xmlns="http://www.netex.org.uk/netex" id="KVE:TopographicPlace:03" version="1">
                                              <Name lang="nor">Oslo</Name>
                                              <TopographicPlaceType>county</TopographicPlaceType>
                                              <CountryRef ref="NO"/>
                                            </TopographicPlace>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GroupOfTariffZones.class)),
                            examples = @ExampleObject(
                                    name = "Groups of Tariff Zones List",
                                    summary = "Example list of tariff zone groups",
                                    value = """
                                            [
                                              {
                                                "id": "RUT:GroupOfTariffZones:1",
                                                "version": "1",
                                                "name": {"value": "Ruter sonenett", "lang": "nor"},
                                                "members": {
                                                  "tariffZoneRef": [
                                                    {"ref": "RUT:TariffZone:1"},
                                                    {"ref": "RUT:TariffZone:2V"}
                                                  ]
                                                }
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = GroupsOfTariffZonesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Groups of Tariff Zones XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <groupsOfTariffZones xmlns="http://www.netex.org.uk/netex">
                                              <GroupOfTariffZones id="RUT:GroupOfTariffZones:1" version="1">
                                                <Name lang="nor">Ruter sonenett</Name>
                                                <members>
                                                  <TariffZoneRef ref="RUT:TariffZone:1"/>
                                                  <TariffZoneRef ref="RUT:TariffZone:2V"/>
                                                </members>
                                              </GroupOfTariffZones>
                                            </groupsOfTariffZones>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GroupOfTariffZones.class),
                            examples = @ExampleObject(
                                    name = "Group of Tariff Zones",
                                    summary = "Example tariff zone group",
                                    value = """
                                            {
                                              "id": "RUT:GroupOfTariffZones:1",
                                              "version": "1",
                                              "name": {"value": "Ruter sonenett", "lang": "nor"},
                                              "members": {
                                                "tariffZoneRef": [
                                                  {"ref": "RUT:TariffZone:1"},
                                                  {"ref": "RUT:TariffZone:2V"},
                                                  {"ref": "RUT:TariffZone:3V"}
                                                ]
                                              }
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = GroupOfTariffZones.class),
                            examples = @ExampleObject(
                                    name = "Group of Tariff Zones XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <GroupOfTariffZones xmlns="http://www.netex.org.uk/netex" id="RUT:GroupOfTariffZones:1" version="1">
                                              <Name lang="nor">Ruter sonenett</Name>
                                              <members>
                                                <TariffZoneRef ref="RUT:TariffZone:1"/>
                                                <TariffZoneRef ref="RUT:TariffZone:2V"/>
                                              </members>
                                            </GroupOfTariffZones>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FareZone.class)),
                            examples = @ExampleObject(
                                    name = "Fare Zones List",
                                    summary = "Example list of fare zones",
                                    value = """
                                            [
                                              {
                                                "id": "RUT:FareZone:1",
                                                "version": "2",
                                                "name": {"value": "Sone 1", "lang": "nor"},
                                                "transportOrganisationRef": {
                                                  "ref": "RUT:Authority:RUT"
                                                },
                                                "scopingMethod": "EXPLICIT_STOPS"
                                              },
                                              {
                                                "id": "RUT:FareZone:2V",
                                                "version": "1",
                                                "name": {"value": "Sone 2V", "lang": "nor"},
                                                "transportOrganisationRef": {
                                                  "ref": "RUT:Authority:RUT"
                                                }
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = FareZonesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Fare Zones XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <fareZones xmlns="http://www.netex.org.uk/netex">
                                              <FareZone id="RUT:FareZone:1" version="2">
                                                <Name lang="nor">Sone 1</Name>
                                                <TransportOrganisationRef ref="RUT:Authority:RUT"/>
                                                <ScopingMethod>explicitStops</ScopingMethod>
                                              </FareZone>
                                            </fareZones>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = FareZone.class),
                            examples = @ExampleObject(
                                    name = "Fare Zone",
                                    summary = "Example fare zone",
                                    value = """
                                            {
                                              "id": "RUT:FareZone:1",
                                              "version": "2",
                                              "name": {"value": "Sone 1", "lang": "nor"},
                                              "transportOrganisationRef": {
                                                "ref": "RUT:Authority:RUT"
                                              },
                                              "scopingMethod": "EXPLICIT_STOPS",
                                              "members": {
                                                "scheduledStopPointRef": [
                                                  {"ref": "NSR:ScheduledStopPoint:1"},
                                                  {"ref": "NSR:ScheduledStopPoint:2"}
                                                ]
                                              }
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = FareZone.class),
                            examples = @ExampleObject(
                                    name = "Fare Zone XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <FareZone xmlns="http://www.netex.org.uk/netex" id="RUT:FareZone:1" version="2">
                                              <Name lang="nor">Sone 1</Name>
                                              <TransportOrganisationRef ref="RUT:Authority:RUT"/>
                                              <ScopingMethod>explicitStops</ScopingMethod>
                                              <members>
                                                <ScheduledStopPointRef ref="NSR:ScheduledStopPoint:1"/>
                                              </members>
                                            </FareZone>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FareZone.class)),
                            examples = @ExampleObject(
                                    name = "Fare Zone Versions",
                                    summary = "Example list of fare zone versions",
                                    value = """
                                            [
                                              {
                                                "id": "RUT:FareZone:1",
                                                "version": "2",
                                                "name": {"value": "Zone 1", "lang": "nor"},
                                                "transportOrganisationRef": {"ref": "RUT:Authority:RUT"}
                                              },
                                              {
                                                "id": "RUT:FareZone:1",
                                                "version": "1",
                                                "name": {"value": "Zone 1", "lang": "nor"},
                                                "transportOrganisationRef": {"ref": "RUT:Authority:RUT"}
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = FareZonesInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Fare Zone Versions XML",
                                    summary = "Example list of fare zone versions in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <fareZones xmlns="http://www.netex.org.uk/netex">
                                              <FareZone id="RUT:FareZone:1" version="2">
                                                <Name lang="nor">Zone 1</Name>
                                                <TransportOrganisationRef ref="RUT:Authority:RUT"/>
                                              </FareZone>
                                              <FareZone id="RUT:FareZone:1" version="1">
                                                <Name lang="nor">Zone 1</Name>
                                                <TransportOrganisationRef ref="RUT:Authority:RUT"/>
                                              </FareZone>
                                            </fareZones>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = FareZone.class),
                            examples = @ExampleObject(
                                    name = "Fare Zone Version",
                                    summary = "Example specific fare zone version",
                                    value = """
                                            {
                                              "id": "RUT:FareZone:1",
                                              "version": "1",
                                              "name": {"value": "Zone 1", "lang": "nor"},
                                              "transportOrganisationRef": {"ref": "RUT:Authority:RUT"}
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = FareZone.class),
                            examples = @ExampleObject(
                                    name = "Fare Zone Version XML",
                                    summary = "Example specific fare zone version in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <FareZone xmlns="http://www.netex.org.uk/netex" id="RUT:FareZone:1" version="1">
                                              <Name lang="nor">Zone 1</Name>
                                              <TransportOrganisationRef ref="RUT:Authority:RUT"/>
                                            </FareZone>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScheduledStopPoint.class)),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Points List",
                                    summary = "Example list of scheduled stop points",
                                    value = """
                                            [
                                              {
                                                "id": "NSR:ScheduledStopPoint:Q1",
                                                "version": "36",
                                                "name": {"value": "Oslo S Trelastgata"}
                                              },
                                              {
                                                "id": "NSR:ScheduledStopPoint:Q2",
                                                "version": "25",
                                                "name": {"value": "Oslo S Jernbanetorget"}
                                              }
                                            ]
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ScheduledStopPointsInFrame_RelStructure.class),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Points XML",
                                    summary = "Example list in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <scheduledStopPoints xmlns="http://www.netex.org.uk/netex">
                                              <ScheduledStopPoint id="NSR:ScheduledStopPoint:Q1" version="36">
                                                <Name>Oslo S Trelastgata</Name>
                                              </ScheduledStopPoint>
                                              <ScheduledStopPoint id="NSR:ScheduledStopPoint:Q2" version="25">
                                                <Name>Oslo S Jernbanetorget</Name>
                                              </ScheduledStopPoint>
                                            </scheduledStopPoints>
                                            """
                            ))
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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ScheduledStopPoint.class),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Point",
                                    summary = "Example scheduled stop point",
                                    value = """
                                            {
                                              "id": "NSR:ScheduledStopPoint:Q1",
                                              "version": "36",
                                              "name": {"value": "Oslo S Trelastgata"}
                                            }
                                            """
                            )),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ScheduledStopPoint.class),
                            examples = @ExampleObject(
                                    name = "Scheduled Stop Point XML",
                                    summary = "Example in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <ScheduledStopPoint xmlns="http://www.netex.org.uk/netex" id="NSR:ScheduledStopPoint:Q1" version="36">
                                              <Name>Oslo S Trelastgata</Name>
                                            </ScheduledStopPoint>
                                            """
                            ))
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
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = StopPlace.class),
                            examples = @ExampleObject(
                                    name = "Oslo S XML Example",
                                    summary = "Stop place in NeTEx XML format",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <StopPlace xmlns="http://www.netex.org.uk/netex" id="NSR:StopPlace:337" version="25">
                                              <Name lang="no">Oslo S</Name>
                                              <Centroid>
                                                <Location>
                                                  <Longitude>10.7522</Longitude>
                                                  <Latitude>59.9111</Latitude>
                                                </Location>
                                              </Centroid>
                                              <TransportMode>rail</TransportMode>
                                              <StopPlaceType>railStation</StopPlaceType>
                                            </StopPlace>
                                            """
                            ))
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
