package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.product.EditProductQuantityDto;
import com.example.fooddelivery.dto.product.GetProductListDto;
import com.example.fooddelivery.mapper.ProductMapper;
import com.example.fooddelivery.model.Product;
import com.example.fooddelivery.repository.ProductRepository;
import com.example.fooddelivery.service.ProductService;
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
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/product")
@Validated
@Api(description = "PRODUCT OPERATIONS")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @ApiOperation(value = "ADD PRODUCT")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("/{nameProduct}")
    public ResponseEntity<GetProductListDto> addNewProduct(@PathVariable @NotBlank(message = "you have to introduce the name of the product in order to add it to the database") String nameProduct) {

        Product product = productService.addNewProduct(nameProduct);

        return ResponseEntity.ok().body(ProductMapper.ProductToGetProductListDto(product));
    }

    @ApiOperation(value = "EDIT QUANTITIES OF PRODUCTS")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("")
    public ResponseEntity<List<GetProductListDto>> editProductsQuantity(@RequestBody @Valid List<EditProductQuantityDto> productList) {

        List<Product> productList1 =  productService.editProductsQuantity(productList);

        return ResponseEntity.ok().body(productList1.stream().
                map(ProductMapper::ProductToGetProductListDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "DELETE PRODUCT")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @DeleteMapping("/{productName}")
    public ResponseEntity<String> deleteProduct(@PathVariable @NotBlank(message = "you have to introduce the name of the product in order to delete it from the database") String productName) {
        productService.deleteProduct(productName);

        return ResponseEntity.ok().body("Product deleted");
    }

    @ApiOperation(value = "GET INFO ABOUT ALL PRODUCTS")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("")
    public ResponseEntity<List<GetProductListDto>> getProducts(){
        List<GetProductListDto> productList = productService.getProducts();
        return ResponseEntity.ok().body(productList);
    }
}