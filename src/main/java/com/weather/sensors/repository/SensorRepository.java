package com.weather.sensors.repository;

import com.weather.sensors.entity.Sensor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SensorRepository extends ReactiveCrudRepository<Sensor, Long> {

}
