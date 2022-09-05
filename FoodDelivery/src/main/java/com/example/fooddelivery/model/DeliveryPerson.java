package com.example.fooddelivery.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryPersonId;

    private String firstName;

    private String lastName;

    private Integer salary;

    private String availability;

    private Integer scheduleStart;

    private Integer scheduleStop;

    private LocalDate employmentDate;

    private LocalDate lastDayOfSalaryRaise;

    private Integer seniority;

    @OneToMany(mappedBy = "deliveryPerson")
    private List<OrderDelivery> orderList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
