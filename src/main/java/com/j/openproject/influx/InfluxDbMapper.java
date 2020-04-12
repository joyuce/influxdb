package com.j.openproject.influx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.j.openproject.config.InfluxDbConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * InfluxDbMapper
 * 数据映射层
 *
 * @author Joyuce
 * @date 2020年03月16日
 */
@Slf4j
@Component
public class InfluxDbMapper {

    @Autowired
    private InfluxDbConfig influxDbConfig;

    @Autowired
    private InfluxDB influxDB;

    /**
     * 数据插入influx db
     *
     * @param entity 实体类
     */
    public <T> void insert(T entity) {
        try {
            Point point = Point.measurementByPOJO(entity.getClass()).addFieldsFromPOJO(entity).build();
            influxDB.write(point);
        } catch (Exception e) {
            log.error("写入influx db 发生异常", e);
        }
    }

    /**
     * 批量插入
     *
     * @param entityList
     */
    public <T> void insert(List<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        BatchPoints batchPoints = BatchPoints.database(influxDbConfig.getDatabase())
                                             //数据保留时间 策略
                                             //.retentionPolicy(influxDbConfig.getRetentionPolicy())
                                             .build();
        try {
            for (Object entity : entityList) {
                Point point = Point.measurementByPOJO(entity.getClass()).addFieldsFromPOJO(entity).build();
                batchPoints.point(point);
            }
            influxDB.write(batchPoints);
        } catch (Exception e) {
            log.error("写入influx db 发生异常", e);
        }
    }

    /**
     * 查询数据
     *
     * @param querySql 查询sql
     * @param entityClass 数据需要封装的实体类
     * @param <T> 实体类
     * @return
     */
    public <T> List<T> selectList(String querySql, Class<T> entityClass) {
        List<T> rs = new ArrayList<>();
        try {
            Query query = new Query(querySql, influxDbConfig.getDatabase());
            QueryResult queryResult = influxDB.query(query);
            List<QueryResult.Result> resultList = queryResult.getResults();
            for (QueryResult.Result result : resultList) {
                List<QueryResult.Series> seriesList = result.getSeries();
                if (CollectionUtils.isEmpty(seriesList)) {
                    return rs;
                }
                for (QueryResult.Series series : seriesList) {
                    String name = series.getName();
                    Map<String, String> tags = series.getTags();
                    List<String> columns = series.getColumns();
                    String[] keys = columns.toArray(new String[0]);
                    List<List<Object>> values = series.getValues();
                    for (List<Object> value : values) {
                        Map<String, Object> beanMap = new HashMap<>();
                        for (int i = 0; i < keys.length; i++) {
                            beanMap.put(keys[i], value.get(i));
                        }
                        try {
                            String json = JSONObject.toJSONString(beanMap);
                            T t = JSONObject.parseObject(json, entityClass);
                            rs.add(t);
                        } catch (Exception e) {
                            log.error("转换influx db数据 发生异常", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询influx db 发生异常", e);
        }
        return rs;
    }

}
