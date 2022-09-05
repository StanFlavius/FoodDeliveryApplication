package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.product.EditProductQuantityDto;
import com.example.fooddelivery.dto.product.GetProductListDto;
import com.example.fooddelivery.exception.productExp.MenuHasProductToBeDeleted;
import com.example.fooddelivery.exception.productExp.ProductAlreadyExist;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    @Autowired
    MenuService menuService;

    @Autowired
    ProductRepository productRepository;

    public Product addNewProduct(String productName){

        Product productExist = productRepository.findByProductName(productName);
        if (productExist != null)
            throw new ProductAlreadyExist("Product " + productName + " already exists");

        Product product = new Product();
        product.setProductName(productName);
        product.setProductQuantity(0);

        productRepository.save(product);

        return product;
    }

    public List<Product> editProductsQuantity(List<EditProductQuantityDto> productList){
        for (EditProductQuantityDto p: productList) {
            Product productExist = productRepository.findByProductName(p.getProductName());
            if (productExist == null)
                throw new ProductDoesNotExist("Product " + p.getProductName() + " does not exist");
        }

        List<Product> productArrayList = new ArrayList<>();

        for (EditProductQuantityDto p: productList) {
            Product productExist = productRepository.findByProductName(p.getProductName());
            productExist.setProductQuantity(productExist.getProductQuantity() + p.getSuplQuantity());
            productRepository.save(productExist);
            productArrayList.add(productExist);
        }

        return productArrayList;
    }

    public void deleteProduct (String productName){
        Product productExist = productRepository.findByProductName(productName);
        //Optional<Product> opt = Optional.of(productExist);
        if (productExist == null)
            throw new ProductDoesNotExist("Product " + productName + " does not exist");

        List<MenuList> menusWithProduct = productExist.getMenuLists();
//        if (menusWithProduct != null)
//            throw new MenuHasProductToBeDeleted("There is at least one menu which has product " + productName);

        for (MenuList menu:menusWithProduct) {
            List<String> products = new ArrayList<>();
            for (Product product:menu.getProductList()) {
                if (!product.getProductName().equals(productName))
                    products.add(product.getProductName());
            }
            menuService.editProductList(menu.getMenuName(), products);
        }

        productRepository.delete(productExist);
    }

    public List<GetProductListDto> getProducts (){
        List<Product> productList = productRepository.findAll();
        List<GetProductListDto> advProductList = new ArrayList<>();

        for (Product p:productList) {
            GetProductListDto advProduct = new GetProductListDto();
            advProduct.setProductName(p.getProductName());
            advProduct.setQuantity(p.getProductQuantity());

            advProductList.add(advProduct);
        }

        return advProductList;
    }
}
