package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductName(String productName);
}
