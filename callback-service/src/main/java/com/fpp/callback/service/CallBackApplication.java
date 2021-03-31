package com.fpp.callback.service;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 17:26
 */
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class CallBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(CallBackApplication.class,args);
    }
}
