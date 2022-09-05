package com.example.fooddelivery.dto.OrderDelivery;

import lombok.Data;
import org.springframework.web.bind.annotation.PutMapping;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderResponseDto {

    private Integer orderId;

    private List<String> menuList;

    private Integer totalPrice;

    private String location;

    private String deliveryPersonName;

    private String orderTime;

    private String status;

    public  OrderResponseDto(){}

    public OrderResponseDto(Integer orderId, List<String> menuList, Integer totalPrice, String location, String deliveryPersonName, String orderTime, String status) {
        this.orderId = orderId;
        this.menuList = menuList;
        this.totalPrice = totalPrice;
        this.location = location;
        this.deliveryPersonName = deliveryPersonName;
        this.orderTime = orderTime;
        this.status = status;
    }
}
