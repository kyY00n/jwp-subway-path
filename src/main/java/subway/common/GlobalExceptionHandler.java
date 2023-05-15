package subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import subway.exception.DuplicatedLineNameException;
import subway.exception.DuplicatedStationNameException;
import subway.exception.InvalidDistanceException;
import subway.exception.LineNotFoundException;
import subway.exception.NoSuchStationException;
import subway.exception.StationAlreadyExistsException;
import subway.exception.StationEdgeNotFoundException;
import subway.exception.StationNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({DuplicatedLineNameException.class, DuplicatedStationNameException.class,
            InvalidDistanceException.class, StationAlreadyExistsException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({LineNotFoundException.class, NoSuchStationException.class, StationEdgeNotFoundException.class,
            StationNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity.notFound().build();
    }
}
