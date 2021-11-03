package com.weather.sensors.model;


import lombok.Value;

/**
 * DTO for query results. Object that contains sensor_id and average fields.
 */

@Value
public class AvgDto {

    private Integer sensor_id;

    private Double average_recorded;

    private String type;

}
