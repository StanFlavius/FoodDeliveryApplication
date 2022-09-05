package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.delivery.AddDeliveryPersonRequestBodyDto;
import com.example.fooddelivery.dto.delivery.AddDeliveryPersonResponse;
import com.example.fooddelivery.dto.delivery.DeliveryGuyDataDto;
import com.example.fooddelivery.dto.delivery.SalarySituation;
import com.example.fooddelivery.mapper.DeliveryMapper;
import com.example.fooddelivery.model.DeliveryPerson;
import com.example.fooddelivery.service.DeliveryPersonService;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/delivery")
@Validated
@Api(description = "DELIVERY OPERATIONS")
public class DeliveryController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @ApiOperation(value = "HIRE DELIVERY PERSON")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("")
    public ResponseEntity<AddDeliveryPersonResponse> addNewDeliveryPerson(@RequestBody @Valid AddDeliveryPersonRequestBodyDto delivery){
        DeliveryPerson deliveryPerson = deliveryPersonService.addDelivery(delivery);

        return ResponseEntity.ok().body(deliveryMapper.DeliveryPersonToAddDeliveryPersonResponse(deliveryPerson));
    }

    @ApiOperation(value = "EDIT SALARY OF DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editSalary/{id}/{newSalary}")
    public ResponseEntity<AddDeliveryPersonResponse> editSalary(@PathVariable Integer id,
                             @PathVariable @Min(value = 1500, message = "Salary should be at least 1500 RON") Integer newSalary){
        DeliveryPerson deliveryPerson = deliveryPersonService.editSalary(id, newSalary);

        return ResponseEntity.ok().body(deliveryMapper.DeliveryPersonToAddDeliveryPersonResponse(deliveryPerson));
    }

    @ApiOperation(value = "EDIT SCHEDULE OF DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editSchedule/{id}/{schStart}/{schStop}")
    public ResponseEntity<AddDeliveryPersonResponse> editSchedule(@PathVariable Integer id,
                               @PathVariable @NotNull(message = "The beginning of the schedule is mandatory") Integer schStart,
                               @PathVariable @NotNull(message = "The end of the schedule is mandatory")Integer schStop){
        DeliveryPerson deliveryPerson = deliveryPersonService.editSchedule(id, schStart, schStop);

        return ResponseEntity.ok().body(deliveryMapper.DeliveryPersonToAddDeliveryPersonResponse(deliveryPerson));
    }

    @ApiOperation(value = "FIRE DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteDeliveryPerson(@PathVariable Integer id){
        return ResponseEntity.ok().body(deliveryPersonService.deleteDeliveryPerson(id));
    }

    @ApiOperation(value = "GET DATA ABOUT ALL DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/all")
    public ResponseEntity<List<DeliveryGuyDataDto>> getDataAboutAllDeliveryPerson(){
        List<DeliveryGuyDataDto> guyDataDtoList = deliveryPersonService.getDataAllDeliveryPerson();

        return ResponseEntity.ok().body(guyDataDtoList);
    }

    @ApiOperation(value = "GET DATA ABOUT SPECIFIC DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryGuyDataDto> getDataAboutOneDeliveryPerson(@PathVariable Integer id){
        DeliveryGuyDataDto guyDataDto = deliveryPersonService.getDataOneDeliveryPerson(id);

        return ResponseEntity.ok().body(guyDataDto);
    }

    @ApiOperation(value = "CHECK IF ANY DELIVERY PERSON DESERVES A PAY RAISE")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/checkSalaryRaise")
    public ResponseEntity<List<SalarySituation>> checkSalarySituation(){
        List<SalarySituation> salarySituationList = deliveryPersonService.checkSalarySituation();

        return ResponseEntity.ok().body(salarySituationList);
    }
}
