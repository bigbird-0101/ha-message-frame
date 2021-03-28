package com.fpp.order.service.service;

import com.fpp.order.service.dao.OrderDao;
import com.fpp.order.service.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigInteger;
import java.util.List;
/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 17:05
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderDao orderDao;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 下单
     * @param order
     * @return
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String addOrder(Order order) {
        orderDao.addOrder(order);
        //事务提交后再发送消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                doSendMsg(order);
            }
        });
        return order.toString();
    }

    /**
     * 发送消息
     * @param order
     */
    public void doSendMsg(Order order){
        if(logger.isDebugEnabled()){
            logger.debug("order message sending {} ",order);
        }
        try {
            rabbitTemplate.convertAndSend("real-exchange", "real_message_routing_key", order);
            //延迟校验消息的发送
            rabbitTemplate.convertAndSend("check-exchange", "check_message_routing_key", order.getId());
        }catch (Exception e){
            if(logger.isErrorEnabled()){
                logger.error("order message error {} {}",order,e);
            }
            //可能由于网络抖动 导致发送消息失败 可使用mysql或者redis 这里我使用redis  键为Order:OrderMessage:orderId
            redisTemplate.opsForHash().put("Order:OrderMessage:Failed",order.getId().toString(),order);
        }
    }

    /**
     * 定时任务  但是要注意如果是多节点部署 这个将会有多个定时任务
     * 1.使用分布式的定时任务
     * 2.自己解决不依赖其他   介绍一种方案
     *    1.采用数据库乐观锁机制 定时任务启动时，去获取锁 获取到了锁才能有权利去执行定时任务
     */
    @Scheduled(cron ="*/15 * * * * ?")
    public void schedulingOrder(){
          if(getLock()){
              try {
                  List<Object> values = redisTemplate.opsForHash().values("Order:OrderMessage:Failed");
                  assert values != null;
                  for (Object orderObj : values) {
                      Order order = (Order) orderObj;
                      doSendMsg(order);
                  }
              }finally {
                  //解锁
              }
          }
    }

    /**
     * 这里不做多写  默认获取到锁
     * @return
     */
    public boolean getLock(){
        return true;
    }

    /**
     * 取消订单
     *
     * @param order
     * @return
     */
    @Override
    @Transactional
    public String cancelOrder(Order order) {
        return orderDao.updateOrder(order.getId(),0)==1?"success":"failed";
    }

    /**
     * 订单支付
     *
     * @param order
     * @return
     */
    @Override
    @Transactional
    public String payOrder(Order order) {
        return orderDao.updateOrder(order.getId(),1)==1?"success":"failed";

    }

    /**
     * 获取订单信息
     *
     * @param orderId
     * @return
     */
    @Override
    public Order getOrder(BigInteger orderId) {
        return orderDao.getOrder(orderId);
    }

    /**
     * 补发消息
     *
     * @param orderId
     */
    @Override
    public String reissueMessage(BigInteger orderId) {
        Order order = getOrder(orderId);
        if(logger.isInfoEnabled()){
            logger.info("补发消息 {}",order);
        }
        if(order!=null) {
            doSendMsg(order);
        }
        return "success";
    }
}
