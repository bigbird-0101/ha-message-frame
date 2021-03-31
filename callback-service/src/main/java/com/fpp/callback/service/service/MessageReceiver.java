package com.fpp.callback.service.service;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
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
    private static final String DEFAULT_MESSAGE_SUCCESS="Order:OrderMessage:Success";

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    RestTemplate restTemplate;


    /**
     * 处理监听校验消息队列
     * @param orderId
     * @param channel
     * @param headers
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "check_dead_message_queue", durable = "true"),
            exchange = @Exchange(value = "delay-exchange"),
            key = "check_message_routing_key"
    ))
    @RabbitHandler
    public void onCheckMessage(@Payload BigInteger orderId, Channel channel, @Headers Map<String, Object> headers) {
        try {
            Boolean hasKey = redisTemplate.hasKey(DEFAULT_MESSAGE_SUCCESS + orderId.toString());
            if(null==hasKey||!hasKey){
                 //消息处理未成功 重新发送消息  这里要做个阀值 避免无限次重复发送
                logger.info("延迟校验消息收到,消息处理未成功 重新发送消息 {}",orderId);
                restTemplate.postForEntity("Http://localhost:8083/reissueMessage?orderId="+orderId,null,String.class);
            }else{
                logger.info("延迟校验消息收到,消息处理已处理成功 {}",orderId);
            }
            long deliverTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliverTag, false);
        } catch (Exception e) {
            logger.error("消息处理异常,消息发送失败消息体 {},异常内容: {}", orderId, e);
            try {
                channel.basicNack((long) headers.get(AmqpHeaders.DELIVERY_TAG),false,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 处理确认消息队列
     * @param orderId
     * @param channel
     * @param headers
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "confirm_message_queue", durable = "true"),
            exchange = @Exchange(value = "confirm-exchange"),
            key = "confirm_message_routing_key"
    ))
    @RabbitHandler
    public void onConfirmMessage(@Payload BigInteger orderId, Channel channel, @Headers Map<String, Object> headers) {
        try {
            if(logger.isInfoEnabled()){
                logger.info("确认消息处理收到 {}", orderId);
            }
            //默认设置7天后过期 作为日志保存7天
            redisTemplate.opsForValue().set(DEFAULT_MESSAGE_SUCCESS+orderId.toString(),1,7,TimeUnit.DAYS);
            long deliverTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliverTag, false);
        } catch (Exception e) {
            logger.error("确认消息处理异常,消息发送失败消息体 {},异常内容: {}", orderId, e);
            try {
                channel.basicNack((long) headers.get(AmqpHeaders.DELIVERY_TAG),false,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}