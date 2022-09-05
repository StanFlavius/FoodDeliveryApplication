package com.example.fooddelivery.exception.userExp;

public class EmailExist extends RuntimeException{
    public EmailExist(String m){
        super(m);
    }
}
