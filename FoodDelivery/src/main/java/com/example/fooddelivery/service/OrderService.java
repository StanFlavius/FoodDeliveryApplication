package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.OrderDelivery.NewOrderDto;
import com.example.fooddelivery.dto.OrderDelivery.OrderResponseDto;
import com.example.fooddelivery.exception.MenuExp.MenuDoesNotExist;
import com.example.fooddelivery.exception.orderExp.NoDeliveryPersonAvailvable;
import com.example.fooddelivery.exception.orderExp.NoMoreProducts;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    DeliveryPersonRepository deliveryPersonRepository;
    @Autowired
    UserEntityRepository userEntityRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    OrderMenuAssocRepository orderMenuAssocRepository;
    @Autowired
    ProductRepository productRepository;

    public OrderResponseDto addNewOrder(Integer userId, String loc, List<NewOrderDto> orderMenus) {
        //check valid input
        List<MenuList> finalMenus = new ArrayList<>();
        Integer totalPrice = 0;

        List<Integer> quantities = new ArrayList<>();
        List<String> menuList = new ArrayList<>();
        for (NewOrderDto newOrderDto : orderMenus) {
            menuList.add(newOrderDto.getMenu());
            quantities.add(newOrderDto.getQuantity());
        }

        int cnt = 0;
        for (String menu : menuList) {
            MenuList menuExist = menuRepository.findMenuListByMenuName(menu);
            if (menuExist == null)
               throw new MenuDoesNotExist("Menu " + menu + " does not exist. Check again the list of menus");
            totalPrice += (menuExist.getPrice() * quantities.get(cnt));
            cnt++;
            finalMenus.add(menuExist);
        }

        //check products
        HashMap<String, Integer> map = new HashMap<>();
        for (NewOrderDto newOrder: orderMenus) {
            MenuList menu = menuRepository.findMenuListByMenuName(newOrder.getMenu());
            List<Product> productList = menu.getProductList();
            for (Product product : productList) {
                if(map.containsKey(product.getProductName())){
                    map.put(product.getProductName(), map.get(product.getProductName()) + newOrder.getQuantity());
                }
                else
                    map.put(product.getProductName(), newOrder.getQuantity());
            }
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()){
            String productName = entry.getKey();
            Integer quantity = entry.getValue();
            Integer quantityDB = productRepository.findByProductName(productName).getProductQuantity();
            if(quantity > quantityDB)
                throw new NoMoreProducts("We don't have resources anymore: product " + productName + " is not available");
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()){
            String productName = entry.getKey();
            Integer quantity = entry.getValue();
            Product product = productRepository.findByProductName(productName);
            product.setProductQuantity(product.getProductQuantity() - quantity);
            productRepository.save(product);
        }

        //check available delivery person
        Calendar now = Calendar.getInstance();
        Integer currHour = now.get(Calendar.HOUR_OF_DAY);

        Boolean ok = Boolean.FALSE;
        DeliveryPerson goodDP = new DeliveryPerson();
        List<DeliveryPerson> deliveryPersonList = deliveryPersonRepository.findAll();
        for (DeliveryPerson deliveryPerson : deliveryPersonList) {
            if (currHour >= deliveryPerson.getScheduleStart() &&
                currHour <= deliveryPerson.getScheduleStop()){
                if (deliveryPerson.getAvailability().equals("AVAILABLE")){
                    goodDP = deliveryPerson;
                    ok = Boolean.TRUE;
                }
            }
        }

        if (ok == Boolean.FALSE)
            throw new NoDeliveryPersonAvailvable("No delivery person available. Try again later.");
        goodDP.setAvailability("NOT AVAILABLE");
        deliveryPersonRepository.save(goodDP);

        OrderDelivery newOrder = new OrderDelivery();
        newOrder.setDeliveryPerson(goodDP);
        System.out.println(new Timestamp(System.currentTimeMillis()));
        newOrder.setOrderTime(new Timestamp(System.currentTimeMillis()));
        newOrder.setStatus("PENDING");
        //newOrder.setMenuList(finalMenus);

        UserEntity userEntity = userEntityRepository.getById(userId);
        newOrder.setUser(userEntity);

        newOrder.setLocation(loc);

        orderRepository.save(newOrder);

        for (MenuList menu : finalMenus) {
            Integer quantity = null;
            for (NewOrderDto order : orderMenus) {
                if(menu.getMenuName().equals(order.getMenu()))
                    quantity = order.getQuantity();
            }
            OrderMenuAssoc orderMenuAssoc = new OrderMenuAssoc();
            orderMenuAssoc.setOrder(newOrder);
            orderMenuAssoc.setMenu(menu);
            orderMenuAssoc.setQuantity(quantity);
            orderMenuAssocRepository.save(orderMenuAssoc);
        }

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setLocation(newOrder.getLocation());
        orderResponseDto.setDeliveryPersonName(goodDP.getFirstName() + " " + goodDP.getLastName());
        orderResponseDto.setTotalPrice(totalPrice);
        orderResponseDto.setMenuList(menuList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = sdf.format(newOrder.getOrderTime());
        orderResponseDto.setOrderTime(orderTime);

        return orderResponseDto;
    }

    public OrderDelivery editStatus(Integer orderId) {
        OrderDelivery orderDelivery = orderRepository.getById(orderId);

        orderDelivery.setStatus("COMPLETED");

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(orderDelivery.getDeliveryPerson().getDeliveryPersonId());
        deliveryPerson.setAvailability("AVAILABLE");
        deliveryPersonRepository.save(deliveryPerson);

        orderRepository.save(orderDelivery);

        return orderDelivery;
    }

    public Integer getMoney(String periodTime) {
        List<OrderDelivery> filteredOrderList = new ArrayList<>();

        for (OrderDelivery order : orderRepository.findAll()) {
            if (periodTime.equals("DAY")) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String orderTime = sdf.format(order.getOrderTime());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                String currTime = dtf.format(now);

                if (orderTime.equals(currTime)){
                    filteredOrderList.add(order);
                }
            }
            if (periodTime.equals("MONTH")) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                String orderTime = sdf.format(order.getOrderTime());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");
                LocalDateTime now = LocalDateTime.now();
                String currTime = dtf.format(now);

                if (orderTime.equals(currTime)){
                    filteredOrderList.add(order);
                }
            }
            if (periodTime.equals("YEAR")) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String orderTime = sdf.format(order.getOrderTime());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
                LocalDateTime now = LocalDateTime.now();
                String currTime = dtf.format(now);

                if (orderTime.equals(currTime)){
                    filteredOrderList.add(order);
                }
            }
        }

        Integer totalPrice = 0;
        for (OrderDelivery order : filteredOrderList) {
            List<OrderMenuAssoc> orders = new ArrayList<>();
            orders = orderMenuAssocRepository.getOrderMenuAssocsByOrder(order);

            List<Integer> quantities = new ArrayList<>();
            List<MenuList> menus = new ArrayList<>();
            for (OrderMenuAssoc orderMenu : orders){
                quantities.add(orderMenu.getQuantity());
                menus.add(orderMenu.getMenu());
            }

            Integer cnt = 0;
            for (MenuList m : menus) {
                totalPrice += (m.getPrice() * quantities.get(cnt));
                cnt++;
            }
        }

        return totalPrice;
    }

    public List<OrderResponseDto> getAllOrders(){
        List<OrderResponseDto> finalList = new ArrayList<>();

        for (OrderDelivery orderDelivery: orderRepository.findAll()) {
            OrderResponseDto orderResponseDto = new OrderResponseDto();

            DeliveryPerson deliveryPerson = orderDelivery.getDeliveryPerson();
            orderResponseDto.setDeliveryPersonName(deliveryPerson.getFirstName() + " " + deliveryPerson.getLastName());

            orderResponseDto.setLocation(orderDelivery.getLocation());

            Integer totalPrice = 0;
            List<OrderMenuAssoc> orders = new ArrayList<>();
//            final int[] arr = { order.getOrderId()};
//            final Iterable<Integer> i1 = () -> Arrays.stream(arr).iterator();
//            orders = orderMenuAssocRepository.findAllById(i1);
            orders = orderMenuAssocRepository.getOrderMenuAssocsByOrder(orderDelivery);

            List<Integer> quantities = new ArrayList<>();
            List<MenuList> menus = new ArrayList<>();
            for (OrderMenuAssoc orderMenu : orders){
                menus.add(orderMenu.getMenu());
                quantities.add(orderMenu.getQuantity());
            }

            Integer cnt = 0;
            List<String> menuList = new ArrayList<>();
            for (MenuList menu: menus) {
                menuList.add(menu.getMenuName());
                totalPrice = totalPrice + (menu.getPrice() * quantities.get(cnt));
                cnt++;
            }
            orderResponseDto.setMenuList(menuList);
            orderResponseDto.setTotalPrice(totalPrice);
            orderResponseDto.setOrderId(orderDelivery.getOrderId());
            orderResponseDto.setStatus(orderDelivery.getStatus());
            orderResponseDto.setOrderTime(orderDelivery.getOrderTime().toString());

            finalList.add(orderResponseDto);
        }
        return finalList;
    }

    public List<OrderResponseDto> getOrdersUser(Integer userId) {
        UserEntity userEntity = userEntityRepository.findById(userId).get();

        List<OrderResponseDto> finalList = new ArrayList<>();

        for (OrderDelivery orderDelivery: orderRepository.findByUserId(userEntity.getId())) {
            OrderResponseDto orderResponseDto = new OrderResponseDto();

            DeliveryPerson deliveryPerson = orderDelivery.getDeliveryPerson();
            orderResponseDto.setDeliveryPersonName(deliveryPerson.getFirstName() + " " + deliveryPerson.getLastName());

            orderResponseDto.setLocation(orderDelivery.getLocation());

            Integer totalPrice = 0;
            List<OrderMenuAssoc> orders = new ArrayList<>();
            orders = orderMenuAssocRepository.getOrderMenuAssocsByOrder(orderDelivery);

            List<Integer> quantities = new ArrayList<>();
            List<MenuList> menus = new ArrayList<>();
            for (OrderMenuAssoc orderMenu : orders){
                menus.add(orderMenu.getMenu());
                quantities.add(orderMenu.getQuantity());
            }

            int cnt = 0;
            List<String> menuList = new ArrayList<>();
            for (MenuList menu: menus) {
                menuList.add(menu.getMenuName());
                totalPrice = totalPrice + (menu.getPrice() * quantities.get(cnt));
                cnt++;
            }
            orderResponseDto.setMenuList(menuList);
            orderResponseDto.setTotalPrice(totalPrice);
            orderResponseDto.setOrderId(orderDelivery.getOrderId());
            orderResponseDto.setStatus(orderDelivery.getStatus());
            orderResponseDto.setOrderTime(orderDelivery.getOrderTime().toString());

            finalList.add(orderResponseDto);
        }
        return finalList;
    }
}
