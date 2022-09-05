package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.OrderDelivery;
import com.example.fooddelivery.model.OrderMenuAssoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMenuAssocRepository extends JpaRepository<OrderMenuAssoc, Integer> {

    List<OrderMenuAssoc> getOrderMenuAssocsByOrder(OrderDelivery order);

}
