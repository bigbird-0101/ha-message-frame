package com.fpp.up.stream.service;

import com.fpp.order.service.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/25 11:42
 */
@Service
public class DealOrderService {
    private static final Logger logger = LoggerFactory.getLogger(DealOrderService.class);

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    private static final String DEFAULT_MESSAGE_DEALED="Order:OrderMessage:Dealed";

    /**
     * 这个事务非常重要  当redis宕机或者脑裂时，幂等性校验失败，那么将会重复扣钱库存，所以添加库存做回滚。
     * @param order
     */
    @Transactional(rollbackFor = Throwable.class)
    public void doDealOrder(Order order) {
        //做幂等性校验
        Boolean defaultMessageSuccess = redisTemplate.hasKey(DEFAULT_MESSAGE_DEALED+order.getId().toString());
        if(null==defaultMessageSuccess||!defaultMessageSuccess){
            //扣减库存
            logger.info("扣减库存1处理成功 {}",order);
            //设置一分钟过期 视业务而定
            redisTemplate.opsForValue().set(DEFAULT_MESSAGE_DEALED+order.getId().toString(),1,1, TimeUnit.MINUTES);
        }
    }
}
