# mummu

Read-only API for stop place related NeTEx entities.

## Development

Before starting the application locally, make sure to download
a stop-place dataset from https://developer.entur.org/stops-and-timetable-data
and point to it with the `no.entur.mummu.data-file` property
in application.properties.

## API Endpoints

All endpoints support both JSON and XML formats (specify with `Accept` header or use `.json`/`.xml` suffix).

### Groups of Stop Places
- `GET /groups-of-stop-places` - Get groups of stop places
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional)
- `GET /groups-of-stop-places/{id}` - Get a specific group of stop places by ID

### Stop Places
- `GET /stop-places` - Get stop places
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional), `multimodal` (default: both), `transportModes` (optional), `stopPlaceTypes` (optional), `topographicPlaceIds` (optional)
- `GET /stop-places/{id}` - Get a specific stop place by ID
- `GET /stop-places/{id}/scheduled-stop-points` - Get scheduled stop points for a stop place
- `GET /stop-places/{id}/versions` - Get all versions of a stop place
- `GET /stop-places/{id}/versions/{version}` - Get a specific version of a stop place
- `GET /stop-places/{id}/children` - Get child stop places
- `GET /stop-places/{id}/parkings` - Get parkings for a stop place

### Quays
- `GET /quays` - Get quays
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional)
- `GET /quays/{id}` - Get a specific quay by ID
- `GET /quays/{id}/versions` - Get all versions of a quay
- `GET /quays/{id}/versions/{version}` - Get a specific version of a quay
- `GET /quays/{id}/stop-place` - Get the stop place for a specific quay

### Parkings
- `GET /parkings` - Get parkings
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional)
- `GET /parkings/{id}` - Get a specific parking by ID
- `GET /parkings/{id}/versions` - Get all versions of a parking
- `GET /parkings/{id}/versions/{version}` - Get a specific version of a parking

### Topographic Places
- `GET /topographic-places` - Get topographic places
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional)
- `GET /topographic-places/{id}` - Get a specific topographic place by ID
- `GET /topographic-places/{id}/versions` - Get all versions of a topographic place
- `GET /topographic-places/{id}/versions/{version}` - Get a specific version of a topographic place

### Tariff Zones (Deprecated)
- `GET /tariff-zones` - Get tariff zones
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional), `authorityRefs` (optional)
- `GET /tariff-zones/{id}` - Get a specific tariff zone by ID
- `GET /tariff-zones/{id}/versions` - Get all versions of a tariff zone
- `GET /tariff-zones/{id}/versions/{version}` - Get a specific version of a tariff zone

### Groups of Tariff Zones
- `GET /groups-of-tariff-zones` - Get groups of tariff zones
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional)
- `GET /groups-of-tariff-zones/{id}` - Get a specific group of tariff zones by ID

### Fare Zones
- `GET /fare-zones` - Get fare zones
  - Query params: `count` (default: 10), `skip` (default: 0), `ids` (optional), `authorityRefs` (optional)
- `GET /fare-zones/{id}` - Get a specific fare zone by ID
- `GET /fare-zones/{id}/versions` - Get all versions of a fare zone
- `GET /fare-zones/{id}/versions/{version}` - Get a specific version of a fare zone

### Scheduled Stop Points
- `GET /scheduled-stop-points` - Get scheduled stop points
  - Query params: `count` (default: 10), `skip` (default: 0)
- `GET /scheduled-stop-points/{id}` - Get a specific scheduled stop point by ID
