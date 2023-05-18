package subway.ui;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.domain.Distance;
import subway.domain.Fare;
import subway.domain.Station;
import subway.dto.api.ShortestPathResponse;
import subway.dto.service.PathResult;
import subway.exception.ArrivalSameWithDepartureException;
import subway.service.FareService;
import subway.service.PathService;
import subway.service.StationService;

@RestController
@RequestMapping("/path")
public class PathController {
    private final StationService stationService;
    private final PathService pathService;
    private final FareService fareService;

    public PathController(StationService stationService, PathService pathService, FareService fareService) {
        this.stationService = stationService;
        this.pathService = pathService;
        this.fareService = fareService;
    }

    @GetMapping
    public ResponseEntity<ShortestPathResponse> getPath(@RequestParam Long departureStationId,
                                                        @RequestParam Long arrivalStationId) {
        if (departureStationId == arrivalStationId) {
            throw new ArrivalSameWithDepartureException();
        }
        List<Station> stations = stationService.findById(List.of(departureStationId, arrivalStationId));
        Station departure = stations.get(0);
        Station arrival = stations.get(1);
        PathResult pathResult = pathService.getShortestPath(departure, arrival);
        Distance distance = pathResult.getPath().calculateTotalDistance();
        Fare fare = fareService.calculateFareOf(distance);

        ShortestPathResponse shortestPathResponse = ShortestPathResponse.of(departure, arrival, pathResult, fare);

        return ResponseEntity.ok().body(shortestPathResponse);
    }
}
