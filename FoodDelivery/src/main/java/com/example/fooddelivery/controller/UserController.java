package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.user.EditUserLocationDto;
import com.example.fooddelivery.dto.user.EditUserPasswordDto;
import com.example.fooddelivery.model.UserEntity;
import com.example.fooddelivery.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@Api(description = "USER OPERATIONS")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "EDIT LOCATION")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editLoc/{userId}/{loc}")
    public ResponseEntity<EditUserLocationDto> editLocation(@PathVariable Integer userId,
                                                            @PathVariable @NotBlank(message = "New location is required") String loc){
//        userService.editLocation(userId, loc);
//
//        return ResponseEntity.ok().body("Location modified");
        return ResponseEntity.ok().body(userService.editLocation(userId, loc));
    }

    @ApiOperation(value = "EDIT PASSWORD")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editPass/{userId}/{newPassword}")
    public ResponseEntity<EditUserPasswordDto> editPassword(@PathVariable Integer userId,
                                                            @PathVariable @NotBlank(message = "New password is required") String newPassword){
        //userService.editPassword(userId, newPassword);

        //return ResponseEntity.ok().body("Password changed");

        return ResponseEntity.ok().body(userService.editPassword(userId, newPassword));
    }
}
