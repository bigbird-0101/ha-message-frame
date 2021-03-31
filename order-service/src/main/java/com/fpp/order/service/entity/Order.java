package com.fpp.order.service.entity;
import java.io.Serializable;
import java.math.BigInteger;
/**
 * @author fpp
 * @version 1.0
 * @date 2021/3/19 16:42
 */
public class Order implements Serializable {

    private BigInteger id;
    private String content;
    /**
     *  0-已取消 1-支付成功 2-等待支付中 3-支付失败
     */
    private Integer status;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Order() {
    }

    public Order(BigInteger id, String content, Integer status) {
        this.id = id;
        this.content = content;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}