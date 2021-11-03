package com.weather.sensors.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * POJO sensor object that is persisted within MySQL
 */
@Data
@Builder
@Table
public class Sensor {

    @Id
    private Long sensorId;

    @NonNull
    private String countryId;

    @NonNull
    private String cityId;
}
