package com.example.fooddelivery.exception.productExp;

import com.example.fooddelivery.model.MenuList;

public class MenuHasProductToBeDeleted extends RuntimeException{
    public MenuHasProductToBeDeleted(String message) {super(message);}
}

