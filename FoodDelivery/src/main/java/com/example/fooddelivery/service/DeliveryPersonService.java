package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.auth.RegistrationRequest;
import com.example.fooddelivery.dto.delivery.AddDeliveryPersonRequestBodyDto;
import com.example.fooddelivery.dto.delivery.DeliveryGuyDataDto;
import com.example.fooddelivery.dto.delivery.OrderDto;
import com.example.fooddelivery.dto.delivery.SalarySituation;
import com.example.fooddelivery.exception.DeliveryPerson.IncorrectScheduleExp;
import com.example.fooddelivery.exception.userExp.EmailExist;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.repository.DeliveryPersonRepository;
import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderMenuAssocRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderMenuAssocRepository orderMenuAssocRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MenuRepository menuRepository;

    public DeliveryPerson addDelivery(AddDeliveryPersonRequestBodyDto request) {
        if(request.getScheduleStart() > 20)
            throw new IncorrectScheduleExp("The delivery person can not start his job later than 20:00");

        if(request.getScheduleStart() < 6)
            throw new IncorrectScheduleExp("The delivery person can not start his job earlier than 6:00");

        if(request.getScheduleStop() > 0 && request.getScheduleStop() < 6)
            throw new IncorrectScheduleExp("The delivery person can work until 24:00");

        if((request.getScheduleStop() - request.getScheduleStart() < 4) ||
                (request.getScheduleStop() - request.getScheduleStart() > 8))
            throw new IncorrectScheduleExp("The delivery person should work at least 4 hours and at most 8 hours");

        UserEntity userEntity = userService.findByLogin(request.getEmail());
        if (userEntity != null)
            throw new EmailExist("User with email: " + request.getEmail() + " already exists");

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setLocation(null);
        registrationRequest.setEmail(request.getEmail());
        registrationRequest.setPassword(passwordEncoder.encode("123456*"));
        registrationRequest.setFirstName(request.getFirstName());
        registrationRequest.setLastName(request.getLastName());
        userEntity = userService.saveUser(registrationRequest, "ROLE_DELIVERY");

        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setUserEntity(userEntity);
        deliveryPerson.setAvailability("AVAILABLE");
        deliveryPerson.setFirstName(request.getFirstName());
        deliveryPerson.setLastName(request.getLastName());
        deliveryPerson.setSalary(request.getSalary());
        deliveryPerson.setScheduleStart(request.getScheduleStart());
        deliveryPerson.setScheduleStop(request.getScheduleStop());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        deliveryPerson.setEmploymentDate(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPerson.setLastDayOfSalaryRaise(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPerson.setSeniority(0);

        deliveryPersonRepository.save(deliveryPerson);

        return deliveryPerson;
    }

    public DeliveryPerson editSalary(Integer id, Integer newSalary) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.getById(id);
        deliveryPerson.setSalary(newSalary);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        deliveryPerson.setLastDayOfSalaryRaise(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPersonRepository.save(deliveryPerson);

        return deliveryPerson;
    }

    public DeliveryPerson editSchedule(Integer id, Integer schStart, Integer schStop){
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(id);
        if(deliveryPerson == null)
            throw new IncorrectScheduleExp("Delivery person with id: " + id.toString() + " does not exist");

        if(schStart > 20)
            throw new IncorrectScheduleExp("The delivery person can not start his job later than 20:00");

        if(schStart < 6)
            throw new IncorrectScheduleExp("The delivery person can not start his job earlier than 6:00");

        if(schStop > 0 && schStop < 6)
            throw new IncorrectScheduleExp("The delivery person can work until 24:00");

        if((schStop - schStart < 4) ||
                (schStop - schStart > 8))
            throw new IncorrectScheduleExp("The delivery person should work at least 4 hours and at most 8 hours");

        deliveryPerson.setScheduleStart(schStart);
        deliveryPerson.setScheduleStop(schStop);
        deliveryPersonRepository.save(deliveryPerson);

        return deliveryPerson;
    }

    public String deleteDeliveryPerson(Integer id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(id);
        UserEntity userEntity = deliveryPerson.getUserEntity();
        userService.deleteUser(userEntity.getId());
        deliveryPersonRepository.delete(deliveryPersonRepository.getById(id));

        return "Delivery person has been fired";
    }

    public DeliveryGuyDataDto getDataOneDeliveryPerson(Integer id){
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(id);

        DeliveryGuyDataDto finalDelivery = new DeliveryGuyDataDto();

        finalDelivery.setFirstName(deliveryPerson.getFirstName());
        finalDelivery.setLastName(deliveryPerson.getLastName());
        finalDelivery.setOrdersNo(deliveryPerson.getOrderList().size());
        finalDelivery.setScheduleStop(deliveryPerson.getScheduleStop());
        finalDelivery.setSalary(deliveryPerson.getSalary());
        finalDelivery.setScheduleStart(deliveryPerson.getScheduleStart());
        finalDelivery.setEmploymentDate(deliveryPerson.getEmploymentDate());
        finalDelivery.setLastDayOfSalaryRaise(deliveryPerson.getLastDayOfSalaryRaise());
        finalDelivery.setSeniority(- ((int) DAYS.between(LocalDate.now(), deliveryPerson.getEmploymentDate())));
        //finalDelivery.setSeniority(deliveryPerson.getSeniority());

        List<OrderDto> orderDtoList = new ArrayList<>();
        for (OrderDelivery order : deliveryPerson.getOrderList()) {
            List<OrderMenuAssoc> orders = new ArrayList<>();
            orders = orderMenuAssocRepository.getOrderMenuAssocsByOrder(order);

            List<MenuList> menus = new ArrayList<>();
            for (OrderMenuAssoc orderMenu : orders)
                menus.add(orderMenu.getMenu());

            List<String> finalMenuList = new ArrayList<>();
            for (MenuList m : menus) {
                finalMenuList.add(m.getMenuName());
            }

            OrderDto finalOrder = new OrderDto();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String orderTime = sdf.format(order.getOrderTime());
            finalOrder.setOrderTime(orderTime);
            finalOrder.setMenus(finalMenuList);
            orderDtoList.add(finalOrder);
        }
        finalDelivery.setOrders(orderDtoList);

        return finalDelivery;
    }

    public List<DeliveryGuyDataDto> getDataAllDeliveryPerson(){
        List<DeliveryGuyDataDto> deliveryList = new ArrayList<>();
        for (DeliveryPerson deliveryPerson : deliveryPersonRepository.findAll()) {
            DeliveryGuyDataDto finalDelivery = getDataOneDeliveryPerson(deliveryPerson.getDeliveryPersonId());

            deliveryList.add(finalDelivery);
        }

        return deliveryList;
    }

    public List<SalarySituation> checkSalarySituation() {
        List<SalarySituation> salarySituationList = new ArrayList<>();
        for (DeliveryPerson deliveryPerson : deliveryPersonRepository.findAll()) {
            SalarySituation salarySituation = new SalarySituation();

            salarySituation.setFirstName(deliveryPerson.getFirstName());
            salarySituation.setLastName(deliveryPerson.getLastName());
            salarySituation.setOldSalary(deliveryPerson.getSalary());
            salarySituation.setEmploymentDate(deliveryPerson.getEmploymentDate());
            salarySituation.setLastSalaryRaise(deliveryPerson.getLastDayOfSalaryRaise());

            Integer seniority = - ((int) DAYS.between(LocalDate.now(), deliveryPerson.getEmploymentDate()));
            salarySituation.setReason("The employee has a seniority of: " + seniority.toString() + " days. ");
            if((seniority >= 30 && seniority < 60))
                salarySituation.setReason(salarySituation.getReason() + "He/She deserves a 5% pay raise.");
            else if((seniority >= 60 && seniority < 90))
                salarySituation.setReason(salarySituation.getReason() + "He/She deserves a 10% pay raise.");
            else if((seniority >= 90))
                salarySituation.setReason(salarySituation.getReason() + "He/She deserves a 15% pay raise.");
            else
                salarySituation.setReason(salarySituation.getReason() + "He/She does not deserve a pay raise.");
            salarySituationList.add(salarySituation);
        }

        return salarySituationList;
    }
}
