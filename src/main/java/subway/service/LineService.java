package subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.LineDirection;
import subway.domain.Station;
import subway.domain.StationEdge;
import subway.domain.dto.InsertionResult;
import subway.dto.LineRequest;
import subway.dto.StationInsertRequest;
import subway.exception.DuplicatedLineNameException;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;
import subway.repository.LineRepository;
import subway.repository.StationRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Long create(LineRequest lineRequest) {
        final Station upStation = findStationById(lineRequest.getUpStationId());
        final Station downStation = findStationById(lineRequest.getDownStationId());

        final StationEdge upEndEdge = new StationEdge(upStation.getId(), 0);
        final StationEdge downEndEdge = new StationEdge(downStation.getId(), lineRequest.getDistance());

        final Line line = Line.of(lineRequest.getName(), lineRequest.getColor(), List.of(upEndEdge, downEndEdge));

        lineRepository.findByName(line.getName()).ifPresent(lineWithSameName -> {
            throw new DuplicatedLineNameException(line.getName());
        });

        return lineRepository.create(line);
    }

    @Transactional
    public void insertStation(StationInsertRequest stationInsertRequest) {
        Long stationId = stationInsertRequest.getStationId();
        findStationById(stationId);
        Long adjacentStationId = stationInsertRequest.getAdjacentStationId();
        findStationById(adjacentStationId);

        Line line = findLineById(stationInsertRequest.getLineId());

        InsertionResult insertionResult = line.insertStation(stationId, adjacentStationId,
                stationInsertRequest.getDistance(), LineDirection.valueOf(stationInsertRequest.getDirection()));
        StationEdge insertedEdge = insertionResult.getInsertedEdge();

        lineRepository.insertStationEdge(line, insertedEdge);

        Optional.ofNullable(insertionResult.getUpdatedEdge())
                .ifPresent(updatedEdge -> lineRepository.updateStationEdge(line, updatedEdge));
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
    }

    public void deleteStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        if (line.size() == 2 && line.contains(stationId)) {
            lineRepository.deleteById(line.getId());
            return;
        }
        Optional.ofNullable(line.deleteStation(stationId))
                .ifPresent(stationEdge -> lineRepository.updateStationEdge(line, stationEdge));
        lineRepository.deleteStation(line, stationId);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }
}
