package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.product.EditProductQuantityDto;
import com.example.fooddelivery.dto.product.GetProductListDto;
import com.example.fooddelivery.exception.productExp.ProductAlreadyExist;
import com.example.fooddelivery.exception.productExp.ProductDoesNotExist;
import com.example.fooddelivery.model.MenuList;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.repository.ProductRepository;
import net.bytebuddy.matcher.FilterableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("create product - success")
    void addNewProduct() {
        Product product =  new Product();
        product.setProductName("cola");
        product.setProductQuantity(0);

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addNewProduct("cola");
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("create product - error")
    void addNewProductError(){
        Product product =  new Product();
        product.setProductName("cola");
        product.setProductQuantity(0);

        when(productRepository.findByProductName(product.getProductName())).thenReturn(product);

        ProductAlreadyExist exp = assertThrows(ProductAlreadyExist.class,
                () -> productService.addNewProduct("cola"));

        assertNotNull(exp);
        assertEquals("Product cola already exists", exp.getMessage());

        verify(productRepository).findByProductName(product.getProductName());
        verify(productRepository, Mockito.times(0)).save(product);
    }

    @Test
    @DisplayName("delete product - product does not exist")
    void deleteProductExp() {
        Product product =  new Product();
        product.setProductId(1);
        product.setProductName("cola");
        product.setProductQuantity(0);

        when(productRepository.findByProductName(product.getProductName())).thenReturn(null);

        ProductDoesNotExist exp = assertThrows(ProductDoesNotExist.class,
                () -> productService.deleteProduct(product.getProductName()));

        assertNotNull(exp);
        assertEquals("Product cola does not exist", exp.getMessage());

        verify(productRepository).findByProductName(product.getProductName());
        verify(productRepository, Mockito.times(0)).delete(product);
    }

//    @Test
//    @DisplayName("delete product - success")
//    void deleteProduct() {
//        Product product =  new Product();
//        product.setProductId(1);
//        product.setProductName("cola");
//        product.setProductQuantity(0);
//
//        MenuList menuList = new MenuList();
//        menuList.setMenuName("nume");
//        menuList.setProductList(List.of(product));
//        List<String> products = List.of("cola");
//
//        when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
//        lenient().when(menuService.editProductList("nume", products)).thenReturn(menuList);
//        doNothing().when(productRepository).delete(product);
//        productService.deleteProduct(product.getProductName());
//
//        verify(productRepository, times(1)).findByProductName(product.getProductName());
//        verify(productRepository, times(1)).delete(product);
//    }

    @Test
    @DisplayName("edit product quantity - error")
    void editProductQuantity(){
        EditProductQuantityDto prod1 = new EditProductQuantityDto();
        prod1.setProductName("cola");
        prod1.setSuplQuantity(1);
        List<EditProductQuantityDto> list = new ArrayList<>();
        list.add(prod1);

        when(productRepository.findByProductName(prod1.getProductName())).thenReturn(null);

        ProductDoesNotExist exp = assertThrows(ProductDoesNotExist.class,
                () -> productService.editProductsQuantity(list));

        assertNotNull(exp);
        assertEquals("Product cola does not exist", exp.getMessage());

        verify(productRepository).findByProductName(prod1.getProductName());
    }

    @Test
    @DisplayName("edit product quantity - success")
    void editProductQuantitySuccess(){
        EditProductQuantityDto prodEdit = new EditProductQuantityDto();
        prodEdit.setProductName("cola");
        prodEdit.setSuplQuantity(1);
        List<EditProductQuantityDto> list = new ArrayList<>();
        list.add(prodEdit);

        Product prod = new Product();
        prod.setProductName("cola");
        prod.setProductQuantity(1);
        List<Product> productArrayList = new ArrayList<>();
        productArrayList.add(prod);

        Product prodEdited = new Product();
        prodEdited.setProductName("cola");
        prodEdited.setProductQuantity(2);
        List<Product> productArrayListEdited = new ArrayList<>();
        productArrayListEdited.add(prod);

        for (EditProductQuantityDto p: list) {
            when(productRepository.findByProductName(p.getProductName())).thenReturn(prod);
        }

        List<Product> result = productService.editProductsQuantity(list);
        assertNotNull(result);
        assertEquals(productArrayListEdited, result);
    }

    @Test
    @DisplayName("get products")
    void getProducts(){
        //arrange
        Product product =  new Product();
        product.setProductName("cola");
        product.setProductQuantity(1);

        GetProductListDto productDto =  new GetProductListDto();
        productDto.setProductName("cola");
        productDto.setQuantity(1);
        List<GetProductListDto> productListDtos = List.of(productDto);

        when(productRepository.findAll())
                .thenReturn(List.of(product));
        //act
        List<GetProductListDto> result = productService.getProducts();

        //assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productListDtos, result);
    }
}
