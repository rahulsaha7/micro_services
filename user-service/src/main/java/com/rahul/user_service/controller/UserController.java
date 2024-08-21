package com.rahul.user_service.controller;

import com.rahul.user_service.models.UpdateUserRequest;
import com.rahul.user_service.models.UserData;
import com.rahul.user_service.service.IUserService;
import dto.enums.ApiResponseCodes;
import dto.exceptions.ApiException;
import dto.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private IUserService userService;

    @GetMapping("/status/check")
    public ResponseEntity<String> status() {
        return new ResponseEntity<>("User Service is working", HttpStatus.OK);
    }

    @GetMapping("/get-user-data")
    public ResponseEntity<ApiResponse<UserData>> getUserData(HttpServletRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
            true,
            ApiResponseCodes.SUCCESS.getCode(),
            "Fetched User data successfully",
            userService.getUserData(request)
        ), HttpStatus.OK);
    }


    @PostMapping("update-user-details")
    public ResponseEntity<ApiResponse<String>> updateUserDetails(HttpServletRequest request,
        @Valid @RequestBody UpdateUserRequest updateUserRequest, BindingResult result) {

        if (result.hasErrors()) {
            throw new ApiException(
                ApiResponseCodes.INVALID_PARAMS.getTitle(),
                ApiResponseCodes.INVALID_PARAMS.getMessage() + result.getFieldError().getDefaultMessage(),
                ApiResponseCodes.INVALID_PARAMS
            );
        }

        userService.updateUserDetails(updateUserRequest);
        return new ResponseEntity<>(
            new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(), "Data updated successfully", null),
            HttpStatus.OK);

    }



}
