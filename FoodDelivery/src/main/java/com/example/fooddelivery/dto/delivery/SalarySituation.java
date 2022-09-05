package com.example.fooddelivery.dto.delivery;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalarySituation {
    private String firstName;

    private String lastName;

    private Integer oldSalary;

    private String reason;

    private LocalDate employmentDate;

    private LocalDate lastSalaryRaise;
}
