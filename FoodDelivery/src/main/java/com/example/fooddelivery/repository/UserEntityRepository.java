package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
}
