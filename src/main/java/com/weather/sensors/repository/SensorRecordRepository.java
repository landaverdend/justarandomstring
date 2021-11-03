package com.weather.sensors.repository;

import com.weather.sensors.entity.SensorRecord;
import com.weather.sensors.model.AvgDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SensorRecordRepository extends ReactiveCrudRepository<SensorRecord, Long> {


    @Query(value = "SELECT AVG(wind_speed) AS average_recorded, fk_sensor_id AS sensor_id, 'windspeed' AS type FROM sensor_record WHERE fk_sensor_id in (:sensorIds) AND time_stamp >= :startDate AND time_stamp <= :endDate GROUP BY sensor_id")
    public Flux<AvgDto> getAverageWindspeed(List<Integer> sensorIds, String startDate, String endDate);

    @Query(value = "SELECT AVG(temperature) AS average_recorded, fk_sensor_id AS sensor_id, 'temperature' AS type FROM sensor_record WHERE fk_sensor_id in (:sensorIds) AND time_stamp >= :startDate AND time_stamp <= :endDate GROUP BY sensor_id")
    public Flux<AvgDto> getAverageTemperature(List<Integer> sensorIds, String startDate, String endDate);

    @Query(value = "SELECT AVG(humidity) AS average_recorded, fk_sensor_id AS sensor_id, 'humidity' AS type FROM sensor_record WHERE fk_sensor_id in (:sensorIds) AND time_stamp >= :startDate AND time_stamp <= :endDate GROUP BY sensor_id")
    public Flux<AvgDto> getAverageHumidity(List<Integer> sensorIds, String startDate, String endDate);

}
