package com.weather.sensors.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;


/**
 * POJO record object that is persisted within MySQL
 */
@Data
@Builder
public class SensorRecord {

    @Id
    private Long tempId;

    @NonNull
    private Long fkSensorId;

    @NonNull
    private LocalDateTime timeStamp;

    private Double temperature;

    private Double humidity;

    private Double windSpeed;

}
