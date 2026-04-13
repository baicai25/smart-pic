package org.baicai.smart;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@EnableAsync
@MapperScan("org.baicai.smart.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartApplication.class, args);
    }

}
