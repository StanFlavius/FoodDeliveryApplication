package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Integer> {
    DeliveryPerson findByDeliveryPersonId(Integer id);

    DeliveryPerson findDeliveryPersonByUserEntity(Integer id);
}
