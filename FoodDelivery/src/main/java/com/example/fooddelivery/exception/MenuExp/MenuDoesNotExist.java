package com.example.fooddelivery.exception.MenuExp;

public class MenuDoesNotExist extends RuntimeException{
    public MenuDoesNotExist(String m){
        super(m);
    }
}
