package com.example.fooddelivery.dto.delivery;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeliveryGuyDataDto {

    private String firstName;

    private String lastName;

    private Integer salary;

    private LocalDate lastDayOfSalaryRaise;

    private LocalDate employmentDate;

    private Integer seniority;

    private Integer scheduleStart;

    private Integer scheduleStop;

    private Integer ordersNo;

    private List<OrderDto> orders;
}
