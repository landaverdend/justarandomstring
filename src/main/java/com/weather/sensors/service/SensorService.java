package com.weather.sensors.service;

import com.weather.sensors.entity.Sensor;
import com.weather.sensors.entity.SensorRecord;
import com.weather.sensors.model.AvgDto;
import com.weather.sensors.model.SensorRequest;
import com.weather.sensors.repository.SensorRecordRepository;
import com.weather.sensors.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.weather.sensors.constants.SensorConstants.*;


/**
 * @author Nic Landaverde
 * Service class that handles logic for requests to mySQL db.
 */
@Service
@Slf4j
public class SensorService {
    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    SensorRecordRepository sensorRecordRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");

    /**
     * Upsert new sensor entities to mySql
     * @param sensor
     * @return
     */
    public Mono<Sensor> upsertSensor(Sensor sensor) {
        return sensorRepository.save(sensor).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Upsert new sensor records to MySql
     * @param record
     * @return
     */
    public Mono<SensorRecord> upsertSensorRecord(SensorRecord record) {
        return sensorRecordRepository.save(record).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<AvgDto> getSensorData(SensorRequest sensorRequest) {
        if (!validMetricsList(sensorRequest.getMetrics()) || sensorRequest.getSensorIdList() == null) {
            throw new IllegalArgumentException();
        }

        // If no start/end date is specified, then just return sensor data for the past thirty days for all sensor ID's in list.
        if (sensorRequest.getStartDate() == null && sensorRequest.getEndDate() == null) {
            String lastThirtyDays = formatter.format(LocalDateTime.now().minusDays(30));
            return getAverages(sensorRequest.getMetrics(), sensorRequest.getSensorIdList(), lastThirtyDays, formatter.format(LocalDateTime.now())).subscribeOn(Schedulers.boundedElastic());
        } else { // startdate and enddate are defined.
            return getAverages(sensorRequest.getMetrics(), sensorRequest.getSensorIdList(),
                    formatter.format(sensorRequest.getStartDate()),
                    formatter.format(sensorRequest.getEndDate()))
                    .subscribeOn(Schedulers.boundedElastic());
        }
    }


    public Flux<AvgDto> getAverages(List<String> metricRequest, List<Integer> sensorIds, String startDate, String endDate) {

        List<Flux<AvgDto>> streams = new ArrayList<>(metricRequest.size());
        for (String metric : metricRequest) {
            switch (metric) {
                case TEMPERATURE:
                    streams.add(sensorRecordRepository.getAverageTemperature(sensorIds, startDate, endDate));
                    break;
                case HUMIDITY:
                    streams.add(sensorRecordRepository.getAverageHumidity(sensorIds, startDate, endDate));
                    break;
                case WINDSPEED:
                    streams.add(sensorRecordRepository.getAverageWindspeed(sensorIds, startDate, endDate));
                    break;
            }
        }

        return Flux.mergeSequential(streams);
    }

    public boolean validMetricsList(List<String> metrics) {
        if (metrics.size() == 0) return false;
        return metrics.stream().map(String::toLowerCase).allMatch(str -> str.equals(TEMPERATURE) || str.equals(HUMIDITY) || str.equals(WINDSPEED));
    }
}
