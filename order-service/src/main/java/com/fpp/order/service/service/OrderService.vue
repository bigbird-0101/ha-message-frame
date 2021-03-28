package com.fpp.order.service.service;
import com.fpp.order.service.entity.Order;

import java.math.BigInteger;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 16:40
 */
public interface OrderService {

    /**
     * 下单
     * @param order
     * @return
     */
    String addOrder(Order order);

    /**
     * 取消订单
     * @param order
     * @return
     */
    String cancelOrder(Order order);

    /**
     * 订单支付
     * @param order
     * @return
     */
    String payOrder(Order order);


    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    Order getOrder(BigInteger orderId);

    /**
     * 补发消息
     * @param orderId
     */
    String reissueMessage(BigInteger orderId);
}
