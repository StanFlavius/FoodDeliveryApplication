package com.example.fooddelivery.mapper;

import com.example.fooddelivery.dto.delivery.AddDeliveryPersonResponse;
import com.example.fooddelivery.model.DeliveryPerson;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public AddDeliveryPersonResponse DeliveryPersonToAddDeliveryPersonResponse(DeliveryPerson deliveryPerson){
        return new AddDeliveryPersonResponse(
          deliveryPerson.getDeliveryPersonId(),
          deliveryPerson.getFirstName(),
          deliveryPerson.getLastName(),
          deliveryPerson.getSalary(),
          deliveryPerson.getScheduleStart(),
          deliveryPerson.getScheduleStop(),
          deliveryPerson.getEmploymentDate(),
                deliveryPerson.getLastDayOfSalaryRaise()
        );
    }
}
