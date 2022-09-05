package com.example.fooddelivery.dto.user;

import lombok.Data;

@Data
public class EditUserLocationDto {

    private String email;

    private String newLocation;


    public EditUserLocationDto(String email, String newLocation) {
        this.email = email;
        this.newLocation = newLocation;
    }
}
