package com.fpp.order.service.controller;
import com.fpp.order.service.entity.Order;
import com.fpp.order.service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigInteger;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 16:41
 */
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/addOrder")
    public String addOrder(@RequestBody Order order){
        return orderService.addOrder(order);
    }

    @PostMapping("/cancelOrder")
    public String cancelOrder(@RequestBody Order order){
        return orderService.cancelOrder(order);
    }


    @PostMapping("/payOrder")
    public String payOrder(@RequestBody Order order){
        return orderService.payOrder(order);
    }


    @PostMapping("/reissueMessage")
    public String reissueMessage(BigInteger orderId){
        return orderService.reissueMessage(orderId);
    }


    @GetMapping("/getOrder")
    public Order getOrder(BigInteger orderId){
        return orderService.getOrder(orderId);
    }

}
