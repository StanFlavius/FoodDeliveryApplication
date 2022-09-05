package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.OrderDelivery.NewOrderDto;
import com.example.fooddelivery.dto.OrderDelivery.OrderResponseDto;
import com.example.fooddelivery.mapper.OrderMapper;
import com.example.fooddelivery.model.OrderDelivery;
import com.example.fooddelivery.model.OrderMenuAssoc;
import com.example.fooddelivery.repository.UserEntityRepository;
import com.example.fooddelivery.repository.UserInfoRepository;
import com.example.fooddelivery.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/order")
@Validated
@Api(description = "ORDER OPERATIONS")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    UserEntityRepository userEntityRepository;

    @ApiOperation(value = "MAKE AN ORDER")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("/add")
    public ResponseEntity<OrderResponseDto> addNewOrder(@RequestParam Integer userId,
                                        @RequestParam Optional<String> location,
                                        @RequestBody List<NewOrderDto> orderMenus){
        String loc = null;
        loc = location.orElseGet(() -> userEntityRepository.getById(userId).getUserinfo().getLocation());

        return ResponseEntity.ok().body(orderService.addNewOrder(userId, loc, orderMenus));
    }

    @ApiOperation(value = "EDIT ORDER STATUS")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/edit/{orderId}")
    public ResponseEntity<OrderResponseDto> editOrderStatus(@PathVariable Integer orderId){
        OrderDelivery orderDelivery = orderService.editStatus(orderId);

        return ResponseEntity.ok().body(orderMapper.OrderToOrderResponseDto(orderDelivery));
    }

    @ApiOperation(value = "GET EARNINGS OF CURRENT DAY/MONTH/YEAR")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/money/{periodTime}")
    public ResponseEntity<String> getWinningsPerPeriod(@PathVariable @NotBlank(message = "Select one of DAY, MONTH or YEAR") String periodTime){
        String result = null;
        if(periodTime.equals("DAY"))
            result = "Total money won today is: ";
        if(periodTime.equals("MONTH"))
            result = "Total money won this month is: ";
        if(periodTime.equals("YEAR"))
            result = "Total money won this year is: ";
        result += String.valueOf(orderService.getMoney(periodTime));
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "GET INFO ABOUT ALL ORDERS")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponseDto>> getOrders(){
        return ResponseEntity.ok().body(orderService.getAllOrders());
    }

    @ApiOperation(value = "GET ORDERS PER USER")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/getOne/{id}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersUser(@PathVariable Integer id){
        return ResponseEntity.ok().body(orderService.getOrdersUser(id));
    }
}
