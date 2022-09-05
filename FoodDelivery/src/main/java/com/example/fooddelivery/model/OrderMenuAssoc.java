package com.example.fooddelivery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class OrderMenuAssoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderDelivery order;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuList menu;

    private Integer quantity;
}
