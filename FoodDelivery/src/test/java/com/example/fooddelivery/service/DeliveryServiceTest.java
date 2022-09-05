package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.auth.RegistrationRequest;
import com.example.fooddelivery.dto.delivery.AddDeliveryPersonRequestBodyDto;
import com.example.fooddelivery.dto.delivery.DeliveryGuyDataDto;
import com.example.fooddelivery.dto.delivery.OrderDto;
import com.example.fooddelivery.dto.delivery.SalarySituation;
import com.example.fooddelivery.exception.DeliveryPerson.IncorrectScheduleExp;
import com.example.fooddelivery.exception.productExp.ProductAlreadyExist;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.exception.userExp.EmailExist;
import com.example.fooddelivery.model.*;
import com.example.fooddelivery.repository.DeliveryPersonRepository;
import com.example.fooddelivery.repository.OrderMenuAssocRepository;
import com.example.fooddelivery.repository.ProductRepository;
import com.example.fooddelivery.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static java.time.temporal.ChronoUnit.DAYS;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {
    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrderMenuAssocRepository orderMenuAssocRepository;

    @InjectMocks
    private DeliveryPersonService deliveryPersonService;

    @Test
    @DisplayName("add delivery person - error schedule start 1")
    void addNewDeliveryError1() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(21);
        add.setScheduleStop(23);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("The delivery person can not start his job later than 20:00", exp.getMessage());
    }

    @Test
    @DisplayName("add delivery person - error schedule start 2")
    void addNewDeliveryError2() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(5);
        add.setScheduleStop(23);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("The delivery person can not start his job earlier than 6:00", exp.getMessage());
    }

    @Test
    @DisplayName("add delivery person - error nr of hours")
    void addNewDeliveryError3() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(17);
        add.setScheduleStop(19);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("The delivery person should work at least 4 hours and at most 8 hours", exp.getMessage());
    }

    @Test
    @DisplayName("add delivery person - error nr of hours 2")
    void addNewDeliveryError6() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(10);
        add.setScheduleStop(19);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("The delivery person should work at least 4 hours and at most 8 hours", exp.getMessage());
    }

    @Test
    @DisplayName("add delivery person - error schedule end")
    void addNewDeliveryError4() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(17);
        add.setScheduleStop(1);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("The delivery person can work until 24:00", exp.getMessage());
    }

    @Test
    @DisplayName("add delivery person - error email exists")
    void addNewDeliveryError5() {
        AddDeliveryPersonRequestBodyDto add = new AddDeliveryPersonRequestBodyDto();
        add.setScheduleStart(8);
        add.setScheduleStop(16);
        add.setSalary(1600);
        add.setFirstName("a");
        add.setLastName("a");
        add.setEmail("asd@asd.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("asd@asd.com");

        when(userService.findByLogin(userEntity.getEmail())).thenReturn(userEntity);

        EmailExist exp = assertThrows(EmailExist.class,
                () -> deliveryPersonService.addDelivery(add));

        assertNotNull(exp);
        assertEquals("User with email: asd@asd.com already exists", exp.getMessage());
        verify(userService).findByLogin(userEntity.getEmail());
    }

    @Test
    @DisplayName("add delivery person - success")
    void addNewDeliverySuccess() {
        AddDeliveryPersonRequestBodyDto registrationRequest = new AddDeliveryPersonRequestBodyDto();
        registrationRequest.setEmail("a");
        registrationRequest.setFirstName("a");
        registrationRequest.setLastName("a");
        registrationRequest.setScheduleStart(10);
        registrationRequest.setScheduleStop(16);
        registrationRequest.setSalary(1000);

        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("a");
        request.setFirstName("a");
        request.setLastName("a");
        request.setPassword(passwordEncoder.encode("123456*"));
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("a");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_DELIVERY");
        userEntity.setRoleEntity(roleEntity);

        when(userService.saveUser(request, "ROLE_DELIVERY")).thenReturn(userEntity);

        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setUserEntity(userEntity);
        deliveryPerson.setAvailability("AVAILABLE");
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(10);
        deliveryPerson.setScheduleStop(16);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        deliveryPerson.setEmploymentDate(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPerson.setLastDayOfSalaryRaise(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPerson.setSeniority(0);

        when(deliveryPersonRepository.save(deliveryPerson)).thenReturn(deliveryPerson);

        DeliveryPerson result = deliveryPersonService.addDelivery(registrationRequest);

        assertNotNull(result);
        assertEquals(deliveryPerson, result);
        verify(deliveryPersonRepository).save(deliveryPerson);
        verify(userService).saveUser(request, "ROLE_DELIVERY");
    }

    @Test
    @DisplayName("delete delivery - success")
    void deleteDelivery() {
        DeliveryPerson deliveryPerson =  new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        deliveryPerson.setUserEntity(userEntity);

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        doNothing().when(userService).deleteUser(1);

        when(deliveryPersonRepository.getById(1)).thenReturn(deliveryPerson);
        doNothing().when(deliveryPersonRepository).delete(deliveryPerson);

        String result = deliveryPersonService.deleteDeliveryPerson(1);

        assertNotNull(result);
        assertEquals("Delivery person has been fired", result);
        verify(userService, times(1)).deleteUser(1);
        verify(deliveryPersonRepository, times(1)).delete(deliveryPerson);
    }

    @Test
    @DisplayName("edit salary")
    void editSalary(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setSalary(1000);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        deliveryPerson.setEmploymentDate(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPerson.setLastDayOfSalaryRaise(LocalDate.parse(formatter.format(LocalDate.now()), formatter));

        Integer newSal = 2000;

        DeliveryPerson deliveryPersonNew = new DeliveryPerson();
        deliveryPersonNew.setDeliveryPersonId(1);
        deliveryPersonNew.setSalary(newSal);

        deliveryPersonNew.setEmploymentDate(LocalDate.parse(formatter.format(LocalDate.now()), formatter));
        deliveryPersonNew.setLastDayOfSalaryRaise(LocalDate.parse(formatter.format(LocalDate.now()), formatter));

        when(deliveryPersonRepository.getById(deliveryPerson.getDeliveryPersonId())).thenReturn(deliveryPerson);
        when(deliveryPersonRepository.save(deliveryPersonNew)).thenReturn(deliveryPersonNew);

        DeliveryPerson result = deliveryPersonService.editSalary(1, 2000);

        assertNotNull(result);
        assertEquals(deliveryPerson, result);

        verify(deliveryPersonRepository).getById(deliveryPerson.getDeliveryPersonId());
        verify(deliveryPersonRepository).save(deliveryPersonNew);
    }

    @Test
    @DisplayName("edit schedule - Error 1")
    void editScheduleError1(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 10;
        Integer schStop = 16;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(null);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("Delivery person with id: 1 does not exist", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - Error 2")
    void editScheduleError2(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 21;
        Integer schStop = 22;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("The delivery person can not start his job later than 20:00", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - Error 3")
    void editScheduleError3(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 5;
        Integer schStop = 10;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("The delivery person can not start his job earlier than 6:00", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - Error 4")
    void editScheduleError4(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 20;
        Integer schStop = 5;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("The delivery person can work until 24:00", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - Error 5")
    void editScheduleError5(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 10;
        Integer schStop = 13;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("The delivery person should work at least 4 hours and at most 8 hours", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - Error 6")
    void editScheduleError6(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(21);
        deliveryPerson.setScheduleStop(23);
        deliveryPerson.setSalary(1600);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");

        Integer schStart = 10;
        Integer schStop = 19;

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        IncorrectScheduleExp exp = assertThrows(IncorrectScheduleExp.class,
                () -> deliveryPersonService.editSchedule(1, schStart, schStop));

        assertNotNull(exp);
        assertEquals("The delivery person should work at least 4 hours and at most 8 hours", exp.getMessage());
    }

    @Test
    @DisplayName("edit schedule - success")
    void editSchedule(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setScheduleStart(10);
        deliveryPerson.setScheduleStop(14);

        Integer schStart = 12;
        Integer schStop = 16;

        DeliveryPerson deliveryPersonNew = new DeliveryPerson();
        deliveryPersonNew.setDeliveryPersonId(1);
        deliveryPersonNew.setScheduleStart(schStart);
        deliveryPersonNew.setScheduleStop(schStop);

        when(deliveryPersonRepository.findByDeliveryPersonId(deliveryPerson.getDeliveryPersonId())).thenReturn(deliveryPerson);
        when(deliveryPersonRepository.save(deliveryPersonNew)).thenReturn(deliveryPersonNew);

        DeliveryPerson result = deliveryPersonService.editSchedule(1, schStart, schStop);

        assertNotNull(result);
        assertEquals(deliveryPerson, result);

        verify(deliveryPersonRepository).findByDeliveryPersonId(deliveryPerson.getDeliveryPersonId());
        verify(deliveryPersonRepository).save(deliveryPersonNew);}

    @Test
    @DisplayName("get data one delivery person")
    void getDataOneDelivery(){
        OrderDto order = new OrderDto();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateSNoMillis = sdf.format(timestamp);
        order.setOrderTime(dateSNoMillis);
        order.setMenus(List.of("menu1"));
        List<OrderDto> orders = List.of(order);

        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setOrderId(1);
        Date date2 = new Date();
        orderDelivery.setOrderTime(new Timestamp(date2.getTime()));
        MenuList menuList = new MenuList();
        menuList.setMenuName("menu1");
        menuList.setMenuId(1);
        OrderMenuAssoc orderMenuAssoc = new OrderMenuAssoc();
        orderMenuAssoc.setId(1);
        orderMenuAssoc.setMenu(menuList);
        orderMenuAssoc.setOrder(orderDelivery);
        orderDelivery.setMenus(List.of(orderMenuAssoc));

        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now());
        deliveryPerson.setOrderList(List.of(orderDelivery));

        DeliveryGuyDataDto finalDelivery = new DeliveryGuyDataDto();
        finalDelivery.setFirstName(deliveryPerson.getFirstName());
        finalDelivery.setLastName(deliveryPerson.getLastName());
        finalDelivery.setOrdersNo(deliveryPerson.getOrderList().size());
        finalDelivery.setScheduleStop(deliveryPerson.getScheduleStop());
        finalDelivery.setSalary(deliveryPerson.getSalary());
        finalDelivery.setScheduleStart(deliveryPerson.getScheduleStart());
        finalDelivery.setEmploymentDate(LocalDate.now());
        finalDelivery.setSeniority(0);
        finalDelivery.setOrders(orders);

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);
        when(orderMenuAssocRepository.getOrderMenuAssocsByOrder(orderDelivery)).thenReturn(List.of(orderMenuAssoc));

        DeliveryGuyDataDto result = deliveryPersonService.getDataOneDeliveryPerson(1);

        assertNotNull(result);
        assertEquals(finalDelivery, result);
    }

//    @Test
//    @DisplayName("get data all delivery")
//    void getDataAllDelivery(){
//        DeliveryPerson deliveryPerson = new DeliveryPerson();
//        deliveryPerson.setDeliveryPersonId(1);
//        deliveryPerson.setFirstName("a");
//        deliveryPerson.setLastName("a");
//        deliveryPerson.setScheduleStop(16);
//        deliveryPerson.setSalary(1000);
//        deliveryPerson.setScheduleStart(12);
//        deliveryPerson.setEmploymentDate(LocalDate.now());
//
//        DeliveryGuyDataDto finalDelivery = new DeliveryGuyDataDto();
//        finalDelivery.setFirstName(deliveryPerson.getFirstName());
//        finalDelivery.setLastName(deliveryPerson.getLastName());
//        finalDelivery.setOrdersNo(deliveryPerson.getOrderList().size());
//        finalDelivery.setScheduleStop(deliveryPerson.getScheduleStop());
//        finalDelivery.setSalary(deliveryPerson.getSalary());
//        finalDelivery.setScheduleStart(deliveryPerson.getScheduleStart());
//        finalDelivery.setEmploymentDate(LocalDate.now());
//        finalDelivery.setSeniority(0);
//
//        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));
//        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);
//        when(deliveryPersonService.getDataOneDeliveryPerson(1)).thenReturn(finalDelivery);
//
//        List<DeliveryGuyDataDto> result = deliveryPersonService.getDataAllDeliveryPerson();
//
//        assertNotNull(result);
//        assertEquals(List.of(finalDelivery), result);
//
//    }

    @Test
    @DisplayName("check salary situation 1")
    void checkSalarySituation1(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now().minusDays(30));

        SalarySituation salarySituation = new SalarySituation();
        salarySituation.setOldSalary(1000);
        salarySituation.setReason("The employee has a seniority of: 30 days. He/She deserves a 5% pay raise.");
        salarySituation.setFirstName("a");
        salarySituation.setLastName("a");
        salarySituation.setEmploymentDate(LocalDate.now().minusDays(30));

        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));

        List<SalarySituation> result = deliveryPersonService.checkSalarySituation();

        assertNotNull(result);
        assertEquals(List.of(salarySituation), result);
    }

    @Test
    @DisplayName("check salary situation 2")
    void checkSalarySituation2(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now().minusDays(60));

        SalarySituation salarySituation = new SalarySituation();
        salarySituation.setOldSalary(1000);
        salarySituation.setReason("The employee has a seniority of: 60 days. He/She deserves a 10% pay raise.");
        salarySituation.setFirstName("a");
        salarySituation.setLastName("a");
        salarySituation.setEmploymentDate(LocalDate.now().minusDays(60));

        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));

        List<SalarySituation> result = deliveryPersonService.checkSalarySituation();

        assertNotNull(result);
        assertEquals(List.of(salarySituation), result);
    }

    @Test
    @DisplayName("check salary situation 3")
    void checkSalarySituation3(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now().minusDays(90));

        SalarySituation salarySituation = new SalarySituation();
        salarySituation.setOldSalary(1000);
        salarySituation.setReason("The employee has a seniority of: 90 days. He/She deserves a 15% pay raise.");
        salarySituation.setFirstName("a");
        salarySituation.setLastName("a");
        salarySituation.setEmploymentDate(LocalDate.now().minusDays(90));

        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));

        List<SalarySituation> result = deliveryPersonService.checkSalarySituation();

        assertNotNull(result);
        assertEquals(List.of(salarySituation), result);
    }

    @Test
    @DisplayName("check salary situation 4")
    void checkSalarySituation4(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now().minusDays(90));

        SalarySituation salarySituation = new SalarySituation();
        salarySituation.setOldSalary(1000);
        salarySituation.setReason("The employee has a seniority of: 90 days. He/She deserves a 15% pay raise.");
        salarySituation.setFirstName("a");
        salarySituation.setLastName("a");
        salarySituation.setEmploymentDate(LocalDate.now().minusDays(90));

        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));

        List<SalarySituation> result = deliveryPersonService.checkSalarySituation();

        assertNotNull(result);
        assertEquals(List.of(salarySituation), result);
    }

    @Test
    @DisplayName("check salary situation 5")
    void checkSalarySituation5(){
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setFirstName("a");
        deliveryPerson.setLastName("a");
        deliveryPerson.setScheduleStop(16);
        deliveryPerson.setSalary(1000);
        deliveryPerson.setScheduleStart(12);
        deliveryPerson.setEmploymentDate(LocalDate.now().minusDays(10));

        SalarySituation salarySituation = new SalarySituation();
        salarySituation.setOldSalary(1000);
        salarySituation.setReason("The employee has a seniority of: 10 days. He/She does not deserve a pay raise.");
        salarySituation.setFirstName("a");
        salarySituation.setLastName("a");
        salarySituation.setEmploymentDate(LocalDate.now().minusDays(10));

        when(deliveryPersonRepository.findAll()).thenReturn(List.of(deliveryPerson));

        List<SalarySituation> result = deliveryPersonService.checkSalarySituation();

        assertNotNull(result);
        assertEquals(List.of(salarySituation), result);
    }
}
