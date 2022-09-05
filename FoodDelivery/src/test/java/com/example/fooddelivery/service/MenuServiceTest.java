package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.menu.AddMenuRequestBodyDto;
import com.example.fooddelivery.exception.MenuExp.MenuAlreadyExist;
import com.example.fooddelivery.exception.MenuExp.MenuDoesNotExist;
import com.example.fooddelivery.exception.MenuExp.ProductListEmpty;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.model.DeliveryPerson;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.model.UserEntity;
import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("delete menu - success")
    void deleteMenu() {
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(menuList);
        doNothing().when(menuRepository).delete(menuList);

        String result = menuService.deleteMenu(menuList.getMenuName());

        assertNotNull(result);
        assertEquals("Menu deleted", result);
        verify(menuRepository, times(1)).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, times(1)).delete(menuList);

        DeliveryPerson deliveryPerson =  new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        deliveryPerson.setUserEntity(userEntity);
    }

    @Test
    @DisplayName("delete menu - menu does not exist")
    void deleteMenuExp() {
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(null);

        MenuDoesNotExist exp = assertThrows(MenuDoesNotExist.class,
                () -> menuService.deleteMenu(menuList.getMenuName()));

        assertNotNull(exp);
        assertEquals("Menu a does not exist", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).delete(menuList);

    }

    @Test
    @DisplayName("edit menu product list - menu does not exist")
    void editProductList() {
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(null);

        MenuDoesNotExist exp = assertThrows(MenuDoesNotExist.class,
                () -> menuService.editProductList(menuList.getMenuName(), new ArrayList<>()));

        assertNotNull(exp);
        assertEquals("Menu a does not exist", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("edit menu product list - product list is empty")
    void editProductList2() {
        Product product = new Product();
        product.setProductName("cola");
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setProductList(List.of(product));

        Product productNew = new Product();
        productNew.setProductName("pepsi");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(menuList);

        ProductListEmpty exp = assertThrows(ProductListEmpty.class,
                () -> menuService.editProductList(menuList.getMenuName(), new ArrayList<>()));

        assertNotNull(exp);
        assertEquals("The new product list is empty", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("edit menu product list - product does not exist")
    void editProductList3() {
        Product product = new Product();
        product.setProductName("cola");
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setProductList(List.of(product));

        Product productNew = new Product();
        productNew.setProductName("pepsi");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(menuList);
        when(productRepository.findByProductName("pepsi")).thenReturn(null);

        ProductDoesNotExist exp = assertThrows(ProductDoesNotExist.class,
                () -> menuService.editProductList(menuList.getMenuName(), List.of("pepsi")));

        assertNotNull(exp);
        assertEquals("The new product wanted to be added, pepsi, does not exist", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(productRepository).findByProductName("pepsi");
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("edit menu product list - success")
    void editProductList4() {
        Product product = new Product();
        product.setProductName("cola");
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setProductList(List.of(product));

        Product productNew = new Product();
        productNew.setProductName("pepsi");
        MenuList menuList2 = new MenuList();
        menuList2.setMenuName("a");
        menuList2.setProductList(List.of(productNew));

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(menuList);
        when(productRepository.findByProductName("pepsi")).thenReturn(productNew);

        MenuList result = menuService.editProductList("a", List.of("pepsi"));

        assertNotNull(result);
        assertEquals(menuList2, result);

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(productRepository).findByProductName("pepsi");
        verify(menuRepository).save(menuList);
    }

    @Test
    @DisplayName("edit menu price - menu does not exist")
    void editPrice() {
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(null);

        MenuDoesNotExist exp = assertThrows(MenuDoesNotExist.class,
                () -> menuService.editPrice(menuList.getMenuName(), 10));

        assertNotNull(exp);
        assertEquals("Menu a does not exist", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("edit menu price - success")
    void editPrice2() {
        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setPrice(10);
        Integer newPrice = 20;

        MenuList menuList2 = new MenuList();
        menuList2.setMenuName("a");
        menuList2.setPrice(newPrice);

        when(menuRepository.findMenuListByMenuName(menuList.getMenuName())).thenReturn(menuList);
        when(menuRepository.save(menuList2)).thenReturn(menuList2);

        MenuList result = menuService.editPrice("a", newPrice);

        assertNotNull(result);
        assertEquals(menuList2, result);

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository).save(menuList2);
    }

    @Test
    @DisplayName("add menu - error menu exists")
    void addMenu1(){
        AddMenuRequestBodyDto addMenuList = new AddMenuRequestBodyDto();
        addMenuList.setMenuName("a");

        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName("a")).thenReturn(menuList);

        MenuAlreadyExist exp = assertThrows(MenuAlreadyExist.class,
                () -> menuService.addMenu(addMenuList));

        assertNotNull(exp);
        assertEquals("Menu a already exists", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("add menu - error product list empty")
    void addMenu2(){
        AddMenuRequestBodyDto addMenuList = new AddMenuRequestBodyDto();
        addMenuList.setMenuName("a");
        addMenuList.setProducts(new ArrayList<>());

        MenuList menuList = new MenuList();
        menuList.setMenuName("a");

        when(menuRepository.findMenuListByMenuName("a")).thenReturn(null);

        ProductListEmpty exp = assertThrows(ProductListEmpty.class,
                () -> menuService.addMenu(addMenuList));

        assertNotNull(exp);
        assertEquals("The product list is empty", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("add menu - error product does not exist")
    void addMenu3(){
        Product product = new Product();
        product.setProductName("cola");

        AddMenuRequestBodyDto addMenuList = new AddMenuRequestBodyDto();
        addMenuList.setMenuName("a");
        addMenuList.setPrice(100);
        addMenuList.setProducts(List.of("cola"));

        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setPrice(100);
        menuList.setProductList(List.of(product));

        when(menuRepository.findMenuListByMenuName("a")).thenReturn(null);
        when(productRepository.findByProductName("cola")).thenReturn(null);

        ProductDoesNotExist exp = assertThrows(ProductDoesNotExist.class,
                () -> menuService.addMenu(addMenuList));

        assertNotNull(exp);
        assertEquals("Product wanted to be added, cola, does not exist", exp.getMessage());

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(productRepository).findByProductName("cola");
        verify(menuRepository, Mockito.times(0)).save(menuList);
    }

    @Test
    @DisplayName("add menu - success")
    void addMenu4(){
        Product product = new Product();
        product.setProductName("cola");

        AddMenuRequestBodyDto addMenuList = new AddMenuRequestBodyDto();
        addMenuList.setMenuName("a");
        addMenuList.setPrice(100);
        addMenuList.setProducts(List.of("cola"));

        MenuList menuList = new MenuList();
        menuList.setMenuName("a");
        menuList.setPrice(100);
        menuList.setProductList(List.of(product));

        when(menuRepository.findMenuListByMenuName("a")).thenReturn(null);
        when(productRepository.findByProductName("cola")).thenReturn(product);
        when(menuRepository.save(menuList)).thenReturn(menuList);

        MenuList result = menuService.addMenu(addMenuList);

        assertNotNull(result);
        assertEquals(menuList, result);

        verify(menuRepository).findMenuListByMenuName(menuList.getMenuName());
        verify(productRepository).findByProductName("cola");
        verify(menuRepository).save(menuList);
    }
}
