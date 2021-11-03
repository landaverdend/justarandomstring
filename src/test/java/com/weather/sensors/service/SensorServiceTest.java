package com.weather.sensors.service;

import com.weather.sensors.model.AvgDto;
import com.weather.sensors.model.SensorRequest;
import com.weather.sensors.repository.SensorRecordRepository;
import com.weather.sensors.repository.SensorRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static com.weather.sensors.constants.SensorConstants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SensorServiceTest {

    private SensorService sensorService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    SensorRequest request;

    @BeforeEach
    public void setUp() {
        request = SensorRequest.builder()
                .sensorIdList(Arrays.asList(1))
                .metrics(Arrays.asList(TEMPERATURE, HUMIDITY, WINDSPEED))
                .build();
        sensorService = new SensorService();
    }

    /**
     * Should throw an IllegalArgument for empty metric list.
     */
    @Test
    public void testGetSensorData1() {
        thrown.expect(IllegalArgumentException.class);
        request.setMetrics(Arrays.asList());
        sensorService.getSensorData(request);
    }

    /**
     * REalistically you dont want to test directly with db
     */
    @Test
    public void testGetSensorData2() {

        Flux<AvgDto> result = sensorService.getSensorData(request);
        result.subscribe( res -> {
            assertNotNull(res);
        });
    }

}
