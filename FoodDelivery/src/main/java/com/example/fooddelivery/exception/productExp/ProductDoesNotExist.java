package com.example.fooddelivery.exception.productExp;

public class ProductDoesNotExist extends RuntimeException{
    public ProductDoesNotExist(String message){
        super(message);
    }
}
