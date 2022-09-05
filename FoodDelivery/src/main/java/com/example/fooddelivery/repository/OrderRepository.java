package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderDelivery, Integer> {

    List<OrderDelivery> findByUserId(Integer userId);

}
