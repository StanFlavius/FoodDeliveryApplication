package com.example.fooddelivery.exception;

import com.example.fooddelivery.exception.DeliveryPerson.IncorrectScheduleExp;
import com.example.fooddelivery.exception.MenuExp.MenuAlreadyExist;
import com.example.fooddelivery.exception.MenuExp.MenuDoesNotExist;
import com.example.fooddelivery.exception.MenuExp.ProductListEmpty;
import com.example.fooddelivery.exception.orderExp.NoDeliveryPersonAvailvable;
import com.example.fooddelivery.exception.orderExp.NoMoreProducts;
import com.example.fooddelivery.exception.productExp.MenuHasProductToBeDeleted;
import com.example.fooddelivery.exception.productExp.ProductAlreadyExist;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.exception.userExp.EmailExist;
import com.example.fooddelivery.exception.userExp.AuthenticationRefused;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handle(ConstraintViolationException exp){
        List<String> errors = exp.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handle(MethodArgumentNotValidException exp){
        List<String> errors = exp.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            ProductAlreadyExist.class,
            ProductDoesNotExist.class,
            MenuAlreadyExist.class,
            MenuDoesNotExist.class,
            ProductListEmpty.class,
            IncorrectScheduleExp.class,
            EmailExist.class,
            NoDeliveryPersonAvailvable.class,
            AuthenticationRefused.class,
            NoMoreProducts.class,
            MenuHasProductToBeDeleted.class

    })
    public ResponseEntity handle(Exception exp){
        return ResponseEntity.badRequest().body(Arrays.asList(exp.getMessage()));
    }


}
