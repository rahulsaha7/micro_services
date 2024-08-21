package com.rahul.user_service.service;

import com.rahul.user_service.models.UpdateUserRequest;
import com.rahul.user_service.models.UserData;
import dto.responses.ApiResponse;
import feign.Param;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("AUTH-SERVICE")
public interface IAuthService {
    @GetMapping("auth/get-user-data")
    public ResponseEntity<ApiResponse<UserData>> getUserData();

    @PostMapping("auth/update-user-details")
    public ResponseEntity<ApiResponse<String>> updateUserDetails(@RequestBody UpdateUserRequest updateUserRequest);
}
