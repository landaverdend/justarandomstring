package com.weather.sensors.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Sensor Request Object.
 */
@Data
@Builder
public class SensorRequest {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<Integer> sensorIdList;

    private List<String> metrics;

}
