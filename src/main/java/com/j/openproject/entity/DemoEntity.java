package com.j.openproject.entity;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import com.alibaba.fastjson.JSON;

import lombok.Data;

/**
 * InfluxDB中,measurement对应于传统关系型数据库中的table(database为配置文件中的log_management).
 * InfluxDB里存储的数据称为时间序列数据,时序数据有零个或多个数据点.
 * 数据点包括time(一个时间戳)，measurement(例如logInfo)，零个或多个tag，其对应于level,module,device_id),至少一个field(即日志内容,msg=something error).
 * InfluxDB会根据tag数值建立时间序列(因此tag数值不能选取诸如UUID作为特征值,易导致时间序列过多,导致InfluxDB崩溃),并建立相应索引,以便优化诸如查询速度.
 */
@Data
@Measurement(name = "demo")
public class DemoEntity {

    /**
     * 时间点 默认当前时间
     *
     */
    @TimeColumn(timeUnit = TimeUnit.MILLISECONDS)
    private Instant time = Instant.ofEpochMilli(System.currentTimeMillis());

    @Column(name = "name", tag = true)
    private String name;

    @Column(name = "style", tag = true)
    private String style;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;

    @Column(name = "state")
    private String state;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
