package com.example.fooddelivery.dto.menu;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MenuResponseDto {

    private String menuName;

    private Integer price;

    private List<String> products;

    public MenuResponseDto(){}

    public MenuResponseDto(String menuName, Integer price, List<String> products) {
        this.menuName = menuName;
        this.price = price;
        this.products = products;
    }
}
