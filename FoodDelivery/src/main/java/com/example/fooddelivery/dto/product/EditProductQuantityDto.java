package com.example.fooddelivery.dto.product;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EditProductQuantityDto {

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    @NotNull(message = "Added quantity is mandatory")
    @Min(value = 5, message = "Added quantity must be greater than or equal to 5")
    private Integer suplQuantity;
}
