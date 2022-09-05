package com.example.fooddelivery.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class MenuList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;

    private String menuName;

    private Integer price;

//    @ManyToMany(mappedBy = "menuList")
//    private List<OrderDelivery> orderList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "menu_product", joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    List<OrderMenuAssoc> orders = new ArrayList<>();
}
