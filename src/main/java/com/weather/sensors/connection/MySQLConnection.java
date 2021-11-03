package com.weather.sensors.connection;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@Configuration
@EnableR2dbcRepositories
public class MySQLConnection extends AbstractR2dbcConfiguration {

    @Value("${mysql.hostName}")
    private String hostName;

    @Value("${mysql.portNumber}")
    private int portNumber;

    @Value("${mysql.database}")
    private String database;

    @Value("${mysql.driver}")
    private String driver;

    @Value("${mysql.username}")
    private String username;

    @Value("${mysql.password}")
    private String password;

    @Primary
    @Bean
    public R2dbcEntityTemplate getSensorTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
         ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.HOST, hostName)
                .option(ConnectionFactoryOptions.PORT, portNumber)
                .option(ConnectionFactoryOptions.DATABASE, database)
                .option(ConnectionFactoryOptions.DRIVER, driver)
                .option(ConnectionFactoryOptions.USER, username)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .build();

        return ConnectionFactories.get(options);
    }

}
