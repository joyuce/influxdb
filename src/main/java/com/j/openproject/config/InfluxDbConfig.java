package com.j.openproject.config;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;

/**
 * @author J
 */
@Getter
@Configuration
public class InfluxDbConfig {

    @Value("${spring.influx.userName}")
    private String userName;

    @Value("${spring.influx.password}")
    private String password;

    @Value("${spring.influx.url}")
    private String url;

    @Value("${spring.influx.database}")
    private String database;

    @Value("${spring.influx.retentionPolicy}")
    private String retentionPolicy;

    @Bean
    @Primary
    public InfluxDB getInfluxDb() {
        InfluxDB influxdb = InfluxDBFactory.connect(this.url, this.userName, this.password);
        //创建库
        influxdb.query(new Query("CREATE DATABASE " + database));
        influxdb.setDatabase(database);
        //设置日志级别
        //influxdb.setLogLevel(InfluxDB.LogLevel.BASIC);
        //（只针对单个插入生效）  开启批量插入 当数据量达到2000 或 时间达到10000 毫秒
        influxdb.enableBatch(2000, 60, TimeUnit.SECONDS);
        return influxdb;
    }

}
