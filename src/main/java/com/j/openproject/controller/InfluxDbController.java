package com.j.openproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import com.j.openproject.annotation.RestPathController;
import com.j.openproject.entity.DemoEntity;
import com.j.openproject.influx.InfluxDbMapper;

/**
 * @author Joyuce
 * @Type InfluxDbController
 * @Desc
 * @date 2020年02月26日
 * @Version V1.0
 */
@RestPathController("/influx")
public class InfluxDbController {

    @Autowired
    private InfluxDbMapper influxDbMapper;

    @GetMapping("/get")
    public List<DemoEntity> se() {
        return influxDbMapper.selectList("select * from demo where style = '1'  ORDER BY time DESC LIMIT 4", DemoEntity.class);
    }

    @GetMapping("/insert")
    public void insert() {
        List<DemoEntity> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoEntity demoEntity = new DemoEntity();
            demoEntity.setName("" + i);
            demoEntity.setStyle("" + i);
            demoEntity.setLat(12.335555f);
            demoEntity.setLon(55.125011f);
            demoEntity.setState("on");
            list.add(demoEntity);
        }
        influxDbMapper.insert(list);

    }

}
