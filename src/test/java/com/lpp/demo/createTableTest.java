package com.lpp.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @version 1.0
 * @desc 类描述
 * @author: panpan.li@okg.com
 * @createTime: 2023年05月09日 13:39
 */
@SpringBootTest
public class createTableTest {


    @Test
    public void initTable() {
        //1，获取progressEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);
    }
}
