package com.fpp.up.stream.service;

import com.fpp.order.service.entity.Order;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/23 9:08
 */
@Component
public class MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    DealOrderService dealOrderService;

    /**
     * 处理订单消息
     * @param order
     * @param channel
     * @param headers
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "real_message_queue", durable = "true"),
            exchange = @Exchange(value = "realExchange", durable = "true"),
            key = "real_message_routing_key"
    ))
    @RabbitHandler
    public void onRealMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headers) {
        try {
            //处理订单
            dealOrderService.doDealOrder(order);
            //发送一个订单处理成功的确认消息
//            if(!order.getId().equals(new BigInteger("85"))){
                //测试85不会被处理 不发送确认消息
                if(logger.isInfoEnabled()) {
                    logger.info("发送确认消息 {}", order);
                }
                rabbitTemplate.convertAndSend("confirm-exchange","confirm_message_routing_key",order.getId());
//            }
            long deliverTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliverTag, false);
        } catch (Exception e) {
            logger.error("消息处理异常,消息发送失败消息体 {},异常内容: {}", order, e);
            try {
                channel.basicNack((long) headers.get(AmqpHeaders.DELIVERY_TAG),false,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}