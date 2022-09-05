package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.menu.AddMenuRequestBodyDto;
import com.example.fooddelivery.dto.menu.GetMenuResponseEntityDto;
import com.example.fooddelivery.dto.product.GetProductListDto;
import com.example.fooddelivery.exception.MenuExp.MenuAlreadyExist;
import com.example.fooddelivery.exception.MenuExp.MenuDoesNotExist;
import com.example.fooddelivery.exception.MenuExp.ProductListEmpty;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MenuService {

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    ProductRepository productRepository;

    public MenuList addMenu (AddMenuRequestBodyDto menu){
        MenuList menuExist = menuRepository.findMenuListByMenuName(menu.getMenuName());
        if(menuExist != null)
            throw new MenuAlreadyExist("Menu " + menu.getMenuName() + " already exists");

        MenuList newMenu = new MenuList();
        newMenu.setMenuName(menu.getMenuName());
        newMenu.setPrice(menu.getPrice());

        if (menu.getProducts().isEmpty())
            throw new ProductListEmpty("The product list is empty");

        List<Product> products = new ArrayList<>();
        for (String product : menu.getProducts()) {
            Product productExist = productRepository.findByProductName(product);
            if (productExist == null)
                throw new ProductDoesNotExist("Product wanted to be added, " + product + ", does not exist");
            products.add(productExist);
        }

        newMenu.setProductList(products);

        menuRepository.save(newMenu);

        return newMenu;
    }

    public MenuList editPrice(String menuName, Integer newPrice){
        MenuList menuExist = menuRepository.findMenuListByMenuName(menuName);
        if(menuExist == null)
            throw new MenuDoesNotExist("Menu " + menuName + " does not exist");

        menuExist.setPrice(newPrice);

        menuRepository.save(menuExist);

        return menuExist;
    }

    public MenuList editProductList(String menuName, List<String> newProductList){
        MenuList menuExist = menuRepository.findMenuListByMenuName(menuName);
        if(menuExist == null)
            throw new MenuDoesNotExist("Menu " + menuName + " does not exist");

        if (newProductList.isEmpty())
            throw new ProductListEmpty("The new product list is empty");

        List<Product> newProducts = new ArrayList<>();
        for (String np : newProductList) {
            Product productExist = productRepository.findByProductName(np);
            if (productExist == null)
                throw new ProductDoesNotExist("The new product wanted to be added, " + np + ", does not exist");
            newProducts.add(productExist);
        }

        menuExist.setProductList(newProducts);

        menuRepository.save(menuExist);

        return menuExist;
    }

    public String deleteMenu(String menuName) {
        MenuList menuExist = menuRepository.findMenuListByMenuName(menuName);
        //Optional<Product> opt = Optional.of(productExist);
        if (menuExist == null)
            throw new MenuDoesNotExist("Menu " + menuName + " does not exist");
        menuRepository.delete(menuExist);

        return "Menu deleted";
    }

    public List<GetMenuResponseEntityDto> getMenus(Integer pageNo,  Integer pageSize,  String sortBy, String dirSort){
//        if (pageNo == null)
//            pageNo = 0;
//        if (pageSize == null)
//            pageSize = 5;
//        if (sortBy == null)
//            sortBy = "menuId";
        if (dirSort.equals("ASC")){
            Page<MenuList> menuLists = menuRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.Direction.ASC, sortBy));

            List<MenuList> menuLists1 = menuLists.getContent();

            List<GetMenuResponseEntityDto> finalList = new ArrayList<>();
            for (MenuList m : menuLists1) {
                GetMenuResponseEntityDto finalMenu = new GetMenuResponseEntityDto();
                finalMenu.setMenuName(m.getMenuName());
                finalMenu.setPrice(m.getPrice());

                List<String> productList = new ArrayList<>();
                for (Product p : m.getProductList()) {
                    productList.add(p.getProductName());
                }

                finalMenu.setProductList(productList);

                finalList.add(finalMenu);
            }

            return finalList;
        }

        Page<MenuList> menuLists = menuRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, sortBy));

        List<MenuList> menuLists1 = menuLists.getContent();

        List<GetMenuResponseEntityDto> finalList = new ArrayList<>();
        for (MenuList m : menuLists1) {
            GetMenuResponseEntityDto finalMenu = new GetMenuResponseEntityDto();
            finalMenu.setMenuName(m.getMenuName());
            finalMenu.setPrice(m.getPrice());

            List<String> productList = new ArrayList<>();
            for (Product p : m.getProductList()) {
                productList.add(p.getProductName());
            }

            finalMenu.setProductList(productList);

            finalList.add(finalMenu);
        }

        return finalList;
    }

//    public List<GetMenuResponseEntityDto> getMenuList(){
//
//        List<MenuList> menuLists = menuRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
//
//        List<GetMenuResponseEntityDto> finalList = new ArrayList<>();
//        for (MenuList m : menuLists) {
//            GetMenuResponseEntityDto finalMenu = new GetMenuResponseEntityDto();
//            finalMenu.setMenuName(m.getMenuName());
//            finalMenu.setPrice(m.getPrice());
//
//            List<String> productList = new ArrayList<>();
//            for (Product p : m.getProductList()) {
//                productList.add(p.getProductName());
//            }
//
//            finalMenu.setProductList(productList);
//
//            finalList.add(finalMenu);
//        }
//
//        return finalList;
//    }
//
//    public List<GetMenuResponseEntityDto> getMenuList2(){
//
//        List<MenuList> menuLists = menuRepository.findAll(Sort.by(Sort.Direction.ASC, "menuName"));
//
//        List<GetMenuResponseEntityDto> finalList = new ArrayList<>();
//        for (MenuList m : menuLists) {
//            GetMenuResponseEntityDto finalMenu = new GetMenuResponseEntityDto();
//            finalMenu.setMenuName(m.getMenuName());
//            finalMenu.setPrice(m.getPrice());
//
//            List<String> productList = new ArrayList<>();
//            for (Product p : m.getProductList()) {
//                productList.add(p.getProductName());
//            }
//
//            finalMenu.setProductList(productList);
//
//            finalList.add(finalMenu);
//        }
//
//        return finalList;
//    }
//
//    public List<GetMenuResponseEntityDto> getMenuList3(){
//
//        List<MenuList> menuLists = menuRepository.findAll();
//
//        List<GetMenuResponseEntityDto> finalList = new ArrayList<>();
//        for (MenuList m : menuLists) {
//            GetMenuResponseEntityDto finalMenu = new GetMenuResponseEntityDto();
//            finalMenu.setMenuName(m.getMenuName());
//            finalMenu.setPrice(m.getPrice());
//
//            List<String> productList = new ArrayList<>();
//            for (Product p : m.getProductList()) {
//                productList.add(p.getProductName());
//            }
//
//            finalMenu.setProductList(productList);
//
//            finalList.add(finalMenu);
//        }
//
//        return finalList;
//    }
}
