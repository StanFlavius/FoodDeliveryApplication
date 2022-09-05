package com.example.fooddelivery.mapper;

import com.example.fooddelivery.dto.menu.MenuResponseDto;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.PortUnreachableException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuMapper {

    @Autowired
    ProductRepository productRepository;

    public MenuResponseDto MenuToMenuResponseDto(MenuList menu){
        List<Product> productList = menu.getProductList();
        List<String> products = productList.stream().map(product -> product.getProductName()).collect(Collectors.toList());

        return new MenuResponseDto(
          menu.getMenuName(),
          menu.getPrice(),
                products
        );
    }
}
