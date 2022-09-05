package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.MenuList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuList, Integer> {

    MenuList findMenuListByMenuName(String menuName);
}
