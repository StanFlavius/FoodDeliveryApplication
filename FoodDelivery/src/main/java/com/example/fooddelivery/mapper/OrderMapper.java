package com.example.fooddelivery.mapper;

import com.example.fooddelivery.dto.OrderDelivery.OrderResponseDto;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.OrderDelivery;
import com.example.fooddelivery.model.OrderMenuAssoc;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDto OrderToOrderResponseDto(OrderDelivery orderDelivery){
        List<String> menuLists = new ArrayList<>();
        Integer totalPrice = 0;
        for (OrderMenuAssoc orderMenu: orderDelivery.getMenus()) {
            menuLists.add(orderMenu.getMenu().getMenuName());
            totalPrice += orderMenu.getMenu().getPrice();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = sdf.format(orderDelivery.getOrderTime());

        return new OrderResponseDto(
                orderDelivery.getOrderId(),
                menuLists,
                totalPrice,
                orderDelivery.getLocation(),
                orderDelivery.getDeliveryPerson().getFirstName() + " " + orderDelivery.getDeliveryPerson().getLastName(),
                orderTime,
                orderDelivery.getStatus()
        );
    }
}
