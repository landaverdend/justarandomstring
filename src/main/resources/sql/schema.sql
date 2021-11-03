CREATE TABLE SENSOR (
    sensor_id int NOT NULL AUTO_INCREMENT,
    country_id varchar(255) NOT NULL,
    city_id varchar(255) NOT NULL,
    PRIMARY KEY(sensor_id)
);

CREATE TABLE SENSOR_RECORD (
    temp_id int NOT NULL AUTO_INCREMENT,
    fk_sensor_id int NOT NULL,
    time_stamp DATETIME NOT NULL,
    temperature double,
    humidity double,
    wind_speed double,
    PRIMARY KEY(temp_id),
    FOREIGN KEY(fk_sensor_id) REFERENCES SENSOR(sensor_id)
);