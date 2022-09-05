package com.example.fooddelivery.dto.menu;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddMenuRequestBodyDto {

    @NotBlank(message = "Menu name is mandatory")
    private String menuName;

    @NotNull(message = "The price is mandatory")
    @Min(value = 10, message = "Price must be greater than 10")
    private Integer price;

    @NotNull(message = "A list of products is required")
    private List<String> products;
}
