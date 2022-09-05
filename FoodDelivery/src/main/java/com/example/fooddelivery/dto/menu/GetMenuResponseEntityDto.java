package com.example.fooddelivery.dto.menu;

import lombok.Data;

import java.util.List;

@Data
public class GetMenuResponseEntityDto {

    String menuName;

    Integer price;

    List<String> productList;
}
