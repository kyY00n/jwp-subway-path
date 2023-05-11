package subway.entity;

import subway.domain.Station;

public class StationEntity {

    private final Long id;
    private final String name;

    public StationEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationEntity from(Station station){
        return new StationEntity(station.getId(), station.getName());
    }

    public Station toDomain() {
        return new Station(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}