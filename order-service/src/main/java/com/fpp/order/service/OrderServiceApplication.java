package com.fpp.order.service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 17:26
 */
@SpringBootApplication
@EnableScheduling // 开启定时任务功能
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class,args);
    }
}
