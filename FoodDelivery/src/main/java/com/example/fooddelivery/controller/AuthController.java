package com.example.fooddelivery.controller;

import com.example.fooddelivery.config.jwt.JwtProvider;
import com.example.fooddelivery.dto.auth.AuthRequest;
import com.example.fooddelivery.dto.auth.AuthResponse;
import com.example.fooddelivery.dto.auth.RegistrationRequest;
import com.example.fooddelivery.exception.userExp.AuthenticationRefused;
import com.example.fooddelivery.model.UserEntity;
import com.example.fooddelivery.service.UserService;
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
import java.util.List;

@Slf4j
@RestController
@Api(description = "AUTHENTICATION OPERATIONS")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @ApiOperation(value = "REGISTER USER")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationRequest> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {

        userService.saveUser(registrationRequest, "ROLE_USER");

        return ResponseEntity.ok().body(registrationRequest);
    }

//    @ApiOperation(value = "GET ALL USERS")
//    @ApiResponses(value = {
//            @ApiResponse(code = 500, message = "Internal server error"),
//            @ApiResponse(code = 200, message = "Successful operation"),
//            @ApiResponse(code = 400, message = "Invalid request"),
//            @ApiResponse(code = 404, message = "Specified resource does not exist")
//    })
//    @GetMapping("/getU")
//    public List<UserEntity> getU(){
//        return userService.getUsers();
//    }

    @ApiOperation(value = "AUTHENTICATE USER/ADMIN/DELIVERY PERSON")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody @Valid AuthRequest request) {
        UserEntity userEntity = userService.findByLoginAndPassword(request.getEmail(), request.getPassword());
        String token = jwtProvider.generateToken(userEntity.getEmail());
        return ResponseEntity.ok().body(token);
    }

    @ApiOperation(value = "TEST ADMIN AUTHENTICATION")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/admin/get")
    public String getAdmin() {
        return "Hi admin";
    }

    @ApiOperation(value = "TEST USER AUTHENTICATION")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/user/get")
    public String getUser() {
        return "Hi user";
    }

    @ApiOperation(value = "TEST DELIVERY PERSON AUTHENTICATION")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("/del/get")
    public String getDelivery() {
        return "Hi delivery";
    }
}
