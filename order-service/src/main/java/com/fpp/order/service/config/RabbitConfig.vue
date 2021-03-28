package com.fpp.order.service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fpp
 */
@Configuration
public class RabbitConfig {
    /**
     * 延迟校验的消息队列
     * @return
     */
    @Bean("rabbitCheckMessageQueue")
    public Queue rabbitCheckMessageQueue(){
        Map<String,Object> params=new HashMap<>(3);
        params.put("x-dead-letter-exchange","delay-exchange");
        params.put("x-dead-letter-routing-key","check_message_routing_key");
        params.put("x-message-ttl",60000);
        return new Queue("check_message_queue",true, false, false,params);
    }

    /**
     * 延迟校验的消息死信队列
     * @return
     */
    @Bean("rabbitCheckMessageDeadQueue")
    public Queue rabbitCheckMessageDeadQueue(){
        return new Queue("check_dead_message_queue",true, false, false);
    }

    @Bean("deadExchange")
    public Exchange deadExchange(){
        return new DirectExchange("delay-exchange");
    }

    @Bean("checkExchange")
    public Exchange checkExchange(){
        return new DirectExchange("check-exchange");
    }

    @Bean("confirmExchange")
    public Exchange confirmExchange(){
        return new DirectExchange("confirm-exchange");
    }

    @Bean("realExchange")
    public Exchange realExchange(){
        return new DirectExchange("real-exchange");
    }

    /**
     * 死信队列与死信exchange和routingkey绑定
     * @return
     */
    @Bean
    public Binding binding(@Qualifier("rabbitCheckMessageDeadQueue") Queue rabbitCheckMessageDeadQueue,@Qualifier("deadExchange") Exchange deadExchange){
        return BindingBuilder.bind(rabbitCheckMessageDeadQueue).to(deadExchange).with("check_message_routing_key").noargs();
    }

    /**
     * 校验消息绑定
     * @return
     */
    @Bean
    public Binding checkBinding(@Qualifier("rabbitCheckMessageQueue") Queue rabbitCheckMessageQueue,@Qualifier("checkExchange") Exchange checkExchange){
        return BindingBuilder.bind(rabbitCheckMessageQueue).to(checkExchange).with("check_message_routing_key").noargs();
    }

    /**
     * 真实绑定
     * @return
     */
    @Bean
    public Binding realBinding(@Qualifier("rabbitRealMessageQueue") Queue rabbitRealMessageQueue,@Qualifier("realExchange") Exchange realExchange){
        return BindingBuilder.bind(rabbitRealMessageQueue).to(realExchange).with("real_message_routing_key").noargs();
    }


    /**
     * 真实消息绑定
     * @return
     */
    @Bean
    public Binding confirmBinding(@Qualifier("rabbitConfirmMessageQueue") Queue rabbitConfirmMessageQueue,@Qualifier("confirmExchange") Exchange confirmExchange){
        return BindingBuilder.bind(rabbitConfirmMessageQueue).to(confirmExchange).with("confirm_message_routing_key").noargs();
    }

    /**
     * 确认消息队列
     * @return
     */
    @Bean("rabbitConfirmMessageQueue")
    public Queue rabbitConfirmMessageQueue(){
        return new Queue("confirm_message_queue");
    }

    /**
     * 真实的消息队列
     * @return
     */
    @Bean("rabbitRealMessageQueue")
    public Queue rabbitRealMessageQueue(){
        return new Queue("real_message_queue");
    }
}
