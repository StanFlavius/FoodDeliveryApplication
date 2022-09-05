package com.example.fooddelivery.dto.delivery;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderDto {

    private String orderTime;

    List<String> menus;
}
