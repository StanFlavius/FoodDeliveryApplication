package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.auth.RegistrationRequest;
import com.example.fooddelivery.dto.user.EditUserLocationDto;
import com.example.fooddelivery.dto.user.EditUserPasswordDto;
import com.example.fooddelivery.exception.userExp.AuthenticationRefused;
import com.example.fooddelivery.exception.userExp.EmailExist;
import com.example.fooddelivery.model.RoleEntity;
import com.example.fooddelivery.model.UserEntity;
import com.example.fooddelivery.model.UserInfo;
import com.example.fooddelivery.repository.RoleEntityRepository;
import com.example.fooddelivery.repository.UserEntityRepository;
import com.example.fooddelivery.repository.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private RoleEntityRepository roleEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public UserEntity saveUser(RegistrationRequest registrationRequest, String role) {
        UserEntity userExistE = userEntityRepository.findByEmail(registrationRequest.getEmail());
        if(userExistE != null)
            throw new EmailExist("A user with email " + registrationRequest.getEmail() + " already exists");

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        userEntity.setEmail(registrationRequest.getEmail());

        RoleEntity userRole = roleEntityRepository.findByName(role);
        userEntity.setRoleEntity(userRole);

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(registrationRequest.getFirstName());
        userInfo.setLastName(registrationRequest.getLastName());
        userInfo.setLocation(registrationRequest.getLocation());
        userInfoRepository.save(userInfo);

        userEntity.setUserinfo(userInfo);

        return userEntityRepository.save(userEntity);
    }

    public UserEntity findByLogin(String login) {
        return userEntityRepository.findByEmail(login);
    }

    public UserEntity findByLoginAndPassword(String login, String password) {
        UserEntity userEntity = userEntityRepository.findByEmail(login);
        if (userEntity != null) {
            if (passwordEncoder.matches(password, userEntity.getPassword())) {
                return userEntity;
            }
            else {
                throw new AuthenticationRefused("Password is incorrect");
            }
        }
        else{
            throw new AuthenticationRefused("User with email: " + login + " is not registered");
        }
    }

    public EditUserLocationDto editLocation(Integer userId, String loc) {
        UserEntity userEntity = userEntityRepository.getById(userId);
        UserInfo userInfo = userInfoRepository.getById(userEntity.getUserinfo().getUserInfoId());
        userInfo.setLocation(loc);
        userInfoRepository.save(userInfo);
        userEntity.setUserinfo(userInfo);
        userEntityRepository.save(userEntity);
        return new EditUserLocationDto(userEntity.getEmail(), loc);
    }

    public void deleteUser(Integer userId){
        userEntityRepository.delete(userEntityRepository.getById(userId));
    }

//    public List<UserEntity> getUsers() {
//        return userEntityRepository.findAll();
//    }

//    public Integer getUserId(String username) {
//        UserEntity userEntity = userEntityRepository.findByEmail(username);
//
//        return userEntity.getId();
//    }

    public EditUserPasswordDto editPassword(Integer userId, String newPassword) {
        UserEntity userEntity = userEntityRepository.getById(userId);

        if(passwordEncoder.matches(newPassword, userEntity.getPassword())){
            throw new EmailExist("The new password matches the old password");
        }
        else{
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userEntityRepository.save(userEntity);
            return new EditUserPasswordDto(userEntity.getEmail(), newPassword);
        }
    }
}
