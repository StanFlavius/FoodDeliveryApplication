package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.OrderDelivery.NewOrderDto;
import com.example.fooddelivery.exception.MenuExp.MenuDoesNotExist;
import com.example.fooddelivery.exception.productExp.ProductAlreadyExist;
import com.example.fooddelivery.model.DeliveryPerson;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.OrderDelivery;
import com.example.fooddelivery.repository.DeliveryPersonRepository;
import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("edit status")
    void editStatus(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setAvailability("NOT AVAILABLE");
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setOrderId(1);
        orderDelivery.setStatus("PENDING");
        orderDelivery.setDeliveryPerson(deliveryPerson);

        DeliveryPerson deliveryPerson2 = new DeliveryPerson();
        deliveryPerson2.setDeliveryPersonId(1);
        deliveryPerson2.setAvailability("AVAILABLE");
        OrderDelivery orderDeliveryDone = new OrderDelivery();
        orderDeliveryDone.setOrderId(1);
        orderDeliveryDone.setStatus("COMPLETED");
        orderDeliveryDone.setDeliveryPerson(deliveryPerson2);

        when(orderRepository.getById(1)).thenReturn(orderDelivery);
        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);
        when(deliveryPersonRepository.save(deliveryPerson2)).thenReturn(deliveryPerson2);
        when(orderRepository.save(orderDeliveryDone)).thenReturn(orderDeliveryDone);

        OrderDelivery result = orderService.editStatus(1);

        assertNotNull(result);
        assertEquals(orderDeliveryDone, result);

        verify(orderRepository).getById(1);
        verify(deliveryPersonRepository).findByDeliveryPersonId(1);
        verify(deliveryPersonRepository).save(deliveryPerson2);
        verify(orderRepository).save(orderDeliveryDone);
    }

    @Test
    @DisplayName("add order - error")
    void addOrderError(){
        NewOrderDto newOrderDto = new NewOrderDto();
        newOrderDto.setMenu("a");
        newOrderDto.setQuantity(1);

        when(menuRepository.findMenuListByMenuName("a")).thenReturn(null);

        MenuDoesNotExist exp = assertThrows(MenuDoesNotExist.class,
                () -> orderService.addNewOrder(1, "a", List.of(newOrderDto)));

        assertNotNull(exp);
        assertEquals("Menu a does not exist. Check again the list of menus", exp.getMessage());

    }
}
