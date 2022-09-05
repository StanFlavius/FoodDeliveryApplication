package com.example.fooddelivery.exception.productExp;

public class ProductAlreadyExist extends RuntimeException{
    public ProductAlreadyExist(String message) {
        super(message);
    }
}
