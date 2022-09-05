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
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserEntityRepository userRepository;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("add user - error email")
    void addNewUserError() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("asd@asd.com");
        registrationRequest.setPassword("asdf");
        registrationRequest.setLocation("asdf");
        registrationRequest.setFirstName("asdf");
        registrationRequest.setLastName("asdf");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("asd@asd.com");

        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(userEntity);

        EmailExist exp = assertThrows(EmailExist.class,
                () -> userService.saveUser(registrationRequest, "ADMIN"));

        assertNotNull(exp);
        assertEquals("A user with email asd@asd.com already exists", exp.getMessage());

        verify(userRepository).findByEmail(registrationRequest.getEmail());
        verify(userRepository, Mockito.times(0)).save(userEntity);
    }

    @Test
    @DisplayName("add user - success")
    void addNewUserSuccess(){
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("asd@asd.com");
        registrationRequest.setPassword("asdf");
        registrationRequest.setLocation("asdf");
        registrationRequest.setFirstName("asdf");
        registrationRequest.setLastName("asdf");
        String role = "ROLE_USER";

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(role);
        when(roleEntityRepository.findByName(role)).thenReturn(roleEntity);

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName("asdf");
        userInfo.setLastName("asdf");
        userInfo.setLocation("asdf");
        when(userInfoRepository.save(userInfo)).thenReturn(userInfo);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("asd@asd.com");
        userEntity.setPassword(passwordEncoder.encode("asdf"));
        userEntity.setRoleEntity(roleEntity);
        userEntity.setUserinfo(userInfo);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserEntity result = userService.saveUser(registrationRequest, role);
        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getRoleEntity().getName(), result.getRoleEntity().getName());
        assertEquals(userEntity.getUserinfo().getLocation(), result.getUserinfo().getLocation());
        verify(userRepository).save(userEntity);
        verify(roleEntityRepository).findByName(role);
        verify(userInfoRepository).save(userInfo);
    }

    @Test
    @DisplayName("delete user")
    void deleteUserTest(){
        Integer userId = 1;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userRepository.getById(userId)).thenReturn(userEntity);
        doNothing().when(userRepository).delete(userEntity);
        userService.deleteUser(userId);

        verify(userRepository, times(1)).getById(userId);
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    @DisplayName("get user by email")
    void findByLoginTest(){
        String email = "asd";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(userEntity);

        UserEntity result = userService.findByLogin(email);

        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("check authentication - success")
    void findByLoginAndPasswordTest(){
        String email = "asd";
        String password = "asd";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(Boolean.TRUE);

        UserEntity result = userService.findByLoginAndPassword(email, password);

        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getPassword(), result.getPassword());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(passwordEncoder).matches(password, userEntity.getPassword());
    }

    @Test
    @DisplayName("check authentication - error password incorrect")
    void findByLoginAndPasswordTestError1(){
        String email = "asd";
        String password = "asd";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode("qwe"));

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(Boolean.FALSE);

        AuthenticationRefused exp = assertThrows(AuthenticationRefused.class,
                () -> userService.findByLoginAndPassword(email, password));

        assertNotNull(exp);
        assertEquals("Password is incorrect", exp.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("check authentication - error user not registered")
    void findByLoginAndPasswordTestError2(){
        String email = "asd";
        String password = "asd";

        when(userRepository.findByEmail(email)).thenReturn(null);

        AuthenticationRefused exp = assertThrows(AuthenticationRefused.class,
                () -> userService.findByLoginAndPassword(email, password));

        assertNotNull(exp);
        assertEquals("User with email: asd is not registered", exp.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("edit location")
    void editLocationTest(){
        Integer id = 1;
        String loc = "asd";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoId(id);
        userInfo.setLocation(loc);
        userEntity.setUserinfo(userInfo);

        String newloc = "qwe";
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(id);
        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserInfoId(id);
        userInfo2.setLocation(newloc);
        userEntity2.setUserinfo(userInfo2);

        when(userRepository.getById(id)).thenReturn(userEntity);
        when(userInfoRepository.getById(id)).thenReturn(userInfo);
        when(userInfoRepository.save(userInfo2)).thenReturn(userInfo2);
        when(userRepository.save(userEntity2)).thenReturn(userEntity2);

        EditUserLocationDto result = userService.editLocation(id, newloc);
        assertNotNull(result);
        assertEquals(userInfo2.getLocation(), result.getNewLocation());
        verify(userRepository).getById(id);
        verify(userInfoRepository).getById(id);
        verify(userInfoRepository).save(userInfo2);
        verify(userRepository).save(userEntity2);

    }

    @Test
    @DisplayName("edit password - success")
    void editPasswordTest(){
        Integer id = 1;
        String email = "asd";
        String oldPassword = "asd";
        String newPassword = "qwe";

        UserEntity userEntity = new UserEntity(passwordEncoder.encode(oldPassword));
        userEntity.setId(id);
        userEntity.setEmail(email);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(id);
        userEntity2.setEmail(email);
        userEntity2.setPassword(passwordEncoder.encode(newPassword));

        when(userRepository.getById(id)).thenReturn(userEntity);
        when(passwordEncoder.matches(newPassword, userEntity.getPassword())).thenReturn(Boolean.FALSE);
        when(userRepository.save(userEntity2)).thenReturn(userEntity2);

        EditUserPasswordDto result = userService.editPassword(id, newPassword);

        assertNotNull(result);
        assertEquals(userEntity2.getEmail(), result.getEmail());
        //assertEquals(userEntity2.getPassword(), result.getNewPassword());
        verify(userRepository).getById(id);
        verify(userRepository).save(userEntity2);
    }

    @Test
    @DisplayName("edit password - error same password")
    void editPasswordTestError(){
        Integer id = 1;
        String password = "asd";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setPassword(passwordEncoder.encode("qwe"));

        when(userRepository.getById(id)).thenReturn(userEntity);
        when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(Boolean.TRUE);

        EmailExist exp = assertThrows(EmailExist.class,
                () -> userService.editPassword(id, password));

        assertNotNull(exp);
        assertEquals("The new password matches the old password", exp.getMessage());
        verify(userRepository).getById(id);
    }
}
