package com.example.fooddelivery.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class GetProductListDto {

    private String productName;

    private Integer quantity;

    public GetProductListDto(){}

    public GetProductListDto(String productName, Integer quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}
