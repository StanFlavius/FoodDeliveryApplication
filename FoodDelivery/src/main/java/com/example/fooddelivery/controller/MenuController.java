package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.menu.AddMenuRequestBodyDto;
import com.example.fooddelivery.dto.menu.GetMenuResponseEntityDto;
import com.example.fooddelivery.dto.menu.MenuResponseDto;
import com.example.fooddelivery.mapper.MenuMapper;
import com.example.fooddelivery.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/menu")
@Validated
@Api(description = "MENU OPERATIONS")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuMapper menuMapper;

    @ApiOperation(value = "ADD MENU")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PostMapping("/add")
    public ResponseEntity<MenuResponseDto> addMenu(@RequestBody @Valid AddMenuRequestBodyDto menu){
        return ResponseEntity.ok().body(menuMapper.MenuToMenuResponseDto(menuService.addMenu(menu)));
    }

    @ApiOperation(value = "EDIT MENU PRICE")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editPrice/{menuName}/{newPrice}")
    public ResponseEntity<MenuResponseDto> editMenuPrice(@PathVariable @NotBlank(message = "The menu name is required") String menuName,
                                @PathVariable
                                    @Min(value = 1, message = "Price must be greater than 0")
                                    @NotNull(message = "The new price is required") Integer newPrice){
        return ResponseEntity.ok().body(menuMapper.MenuToMenuResponseDto(menuService.editPrice(menuName, newPrice)));
    }

    @ApiOperation(value = "EDIT PRODUCT LIST OF MENU")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @PutMapping("/editProductList/{menuName}")
    public ResponseEntity<MenuResponseDto> editProductList(@PathVariable
                                      @NotBlank(message = "The menu name is required") String menuName,
                                  @RequestBody List<String> newProductList){
        return ResponseEntity.ok().body(menuMapper.MenuToMenuResponseDto(menuService.editProductList(menuName, newProductList)));
    }

//    @ApiOperation(value = "DELETE MENU")
//    @ApiResponses(value = {
//            @ApiResponse(code = 500, message = "Internal server error"),
//            @ApiResponse(code = 200, message = "Successful operation"),
//            @ApiResponse(code = 400, message = "Invalid request"),
//            @ApiResponse(code = 404, message = "Specified resource does not exist")
//    })
//    @DeleteMapping("/{menuName}")
//    public ResponseEntity<String> deleteMenu(@PathVariable @NotBlank(message = "you have to introduce the name of the menu in order to delete it from the database") String menuName) {
//
//        return ResponseEntity.ok().body(menuService.deleteMenu(menuName));
//    }

    @ApiOperation(value = "GET ALL MENUS + PAGINATION AND SORTING")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Specified resource does not exist")
    })
    @GetMapping("")
    public ResponseEntity<List<GetMenuResponseEntityDto>> getMenus(@RequestParam Optional<Integer> pageNo,
                                                                  @RequestParam Optional<Integer> pageSize,
                                                                  @RequestParam Optional<String> sortBy,
                                                                  @RequestParam Optional<String> dirSort){
        Integer fPageNo = new Integer(0);
        if(pageNo.isPresent())
            fPageNo = pageNo.get() - 1;
        else
            fPageNo = pageNo.orElse(1);
        Integer fPageSize = pageSize.orElse(5);
        String fSortBy = sortBy.orElse("menuId");
        String fDirSort = dirSort.orElse("ASC");
        return ResponseEntity.ok().body(menuService.getMenus(fPageNo, fPageSize, fSortBy, fDirSort));
    }

}
