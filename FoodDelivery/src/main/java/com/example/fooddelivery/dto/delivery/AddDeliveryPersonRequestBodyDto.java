package com.example.fooddelivery.dto.delivery;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddDeliveryPersonRequestBodyDto {

    @Email(regexp = ".+[@].+[\\.].+", message = "Email format is incorrect")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Salary is mandatory")
    @Min(value = 1500, message = "The salary should be at least 1500 RON")
    private Integer salary;

    @NotNull(message = "The beginning of the schedule is mandatory")
    private Integer scheduleStart;

    @NotNull(message = "The end of the schedule is mandatory")
    private Integer scheduleStop;
}
