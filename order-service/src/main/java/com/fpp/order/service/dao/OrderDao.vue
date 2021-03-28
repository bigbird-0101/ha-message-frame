package com.fpp.order.service.dao;
import com.fpp.order.service.entity.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 16:59
 */
@Mapper
public interface OrderDao {
    @Insert("insert into tab_order (content) value (#{content})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addOrder(Order order);

    @Update("update tab_order set status=#{status} where id=#{orderId}")
    int updateOrder(@Param("orderId") BigInteger orderId, @Param("status") Integer status);

    @Select("select id,content,status from tab_order where id=#{orderId}")
    Order getOrder(BigInteger orderId);
}
