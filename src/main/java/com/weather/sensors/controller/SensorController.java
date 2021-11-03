package com.weather.sensors.controller;

import com.weather.sensors.entity.Sensor;
import com.weather.sensors.entity.SensorRecord;
import com.weather.sensors.model.AvgDto;
import com.weather.sensors.model.SensorRequest;
import com.weather.sensors.service.SensorService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@Data
public class SensorController {

    @Autowired
    private SensorService sensorService;


    /**
     * Given a list of sensorID's and a date range, return a mapping of each sensor to it's average temperature within this range.
     * @param sensorRequest
     * @return
     */
    @PostMapping(path = "/recordList")
    public DeferredResult<ResponseEntity<Object>> getSensorData(@RequestBody SensorRequest sensorRequest) {

        DeferredResult<ResponseEntity<Object>> result = new DeferredResult<>(10000L);

        Map<Integer, Map<String, Double>> recordMapping = new HashMap<>();

        try {
            Flux<AvgDto> sensorRecords = sensorService.getSensorData(sensorRequest);

            sensorRecords.subscribe(avgDto -> {
                if (avgDto != null) {
                    int sensorId = avgDto.getSensor_id();
                    if (recordMapping.get(sensorId) == null) {
                        recordMapping.put(sensorId, new HashMap<>());
                    }
                    if (avgDto.getAverage_recorded() != null) {
                        recordMapping.get(sensorId).put(avgDto.getType(), avgDto.getAverage_recorded());
                    }
                }
            }, throwable -> {
                result.setResult(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
                log.error("Error grabbing report for request: {}", sensorRequest, throwable);
            }, () -> {
                result.setResult(new ResponseEntity<>(recordMapping, HttpStatus.OK));
            });
        } catch (IllegalArgumentException e) {
            result.setResult(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        }

        return result;
    }

    /**
     * Controller method for upserting new sensor records.
     * @param record
     * @return
     */
    @PutMapping(path = "/sensorRecord", consumes = "application/json")
    public DeferredResult<ResponseEntity<Void>> upsertSensorRecord(@RequestBody SensorRecord record) {
        log.info("Received insert request for {}", record);

        DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
        Mono<SensorRecord> monoRecord = sensorService.upsertSensorRecord(record);

        monoRecord.subscribe(sensorResponse -> {
            log.info("Successfully handled record registration for: {}", sensorResponse);
            result.setResult(new ResponseEntity<>(null, HttpStatus.OK));
        }, throwable -> {
            handleUpsertException(throwable, result);
        });

        return result;
    }

    @PutMapping(path = "/sensor", produces = "application/json", consumes = "application/json")
    public DeferredResult<ResponseEntity<Void>> upsertSensor(@RequestBody Sensor sensor) {
        log.info("Received insert request for {}", sensor);

        DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();

        Mono<Sensor> monoSensor = sensorService.upsertSensor(sensor);

        monoSensor.subscribe(sensorResponse -> {
            log.info("Successfully handled sensor registration for: {}", sensorResponse);
            result.setResult(new ResponseEntity<>(null, HttpStatus.OK));
        }, throwable -> {
            handleUpsertException(throwable, result);
        });

        return result;
    }


    /**
     * Handle different exception types depending on error. Delegate response depending on error.
     * @param throwable
     * @param result
     */
    private void handleUpsertException(Throwable throwable, DeferredResult<ResponseEntity<Void>> result) {
        log.info("Error upserting to mySQL");

        if (throwable instanceof DataIntegrityViolationException) {
            log.error("Missing sensor foreign key.");
            result.setResult(new ResponseEntity<>(null, HttpStatus.CONFLICT));
        }
        else if (throwable instanceof TransientDataAccessException) {
            log.error("Requested ID does not exist.");
            result.setResult(new ResponseEntity<>(null, HttpStatus.CONFLICT));
        }
        else {
            log.error("Insert/update failed.", throwable);
            result.setResult(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
