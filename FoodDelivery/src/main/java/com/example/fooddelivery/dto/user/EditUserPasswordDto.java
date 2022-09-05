package com.example.fooddelivery.dto.user;

import lombok.Data;

@Data
public class EditUserPasswordDto {

    private String email;

    private String newPassword;

    public EditUserPasswordDto(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
}
