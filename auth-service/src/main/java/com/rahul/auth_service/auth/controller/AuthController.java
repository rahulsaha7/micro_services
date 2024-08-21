package com.rahul.auth_service.auth.controller;

import com.rahul.auth_service.auth.model.LoginRequest;
import com.rahul.auth_service.auth.model.RegistrationModel;
import com.rahul.auth_service.auth.model.UpdateUserRequest;
import com.rahul.auth_service.auth.model.UserData;
import com.rahul.auth_service.auth.service.IAuthService;
import com.rahul.auth_service.dto.ApiException;
import com.rahul.auth_service.dto.ApiResponse;
import com.rahul.auth_service.dto.ApiResponseCodes;
import com.rahul.auth_service.dto.AuthAction;
import com.rahul.auth_service.dto.AuthModule;
import com.rahul.auth_service.dto.AuthUpgrade;
import com.rahul.auth_service.dto.AuthUpgradeModule;
import com.rahul.auth_service.dto.Authenticate;
import com.rahul.auth_service.utils.PatternUtils;
import com.rahul.auth_service.utils.UtilsConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/")
@Slf4j
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse<String>> register(HttpServletRequest request,
        @Valid @RequestBody RegistrationModel registrationRequest, BindingResult result) {

        log.info("Registration process started");

        if (result.hasErrors()) {
            throw new ApiException(
                ApiResponseCodes.INVALID_PARAMS.getTitle(),
                ApiResponseCodes.INVALID_PARAMS.getMessage() + result.getFieldError().getDefaultMessage(),
                ApiResponseCodes.INVALID_PARAMS
            );
        }

        authService.register(registrationRequest);

        return new ResponseEntity<>(
            new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(), "Registration Successful", null), HttpStatus.OK);

    }


    @PostMapping("login")
    @AuthUpgrade(module = AuthUpgradeModule.AUTH_USER, action = AuthAction.CREATE)
    public ResponseEntity<ApiResponse<String>> login(HttpServletRequest request,
        @Valid @RequestBody LoginRequest loginRequest, BindingResult result) {

        log.info("Login process started");

        if (result.hasErrors()) {
            throw new ApiException(
                ApiResponseCodes.INVALID_PARAMS.getTitle(),
                ApiResponseCodes.INVALID_PARAMS.getMessage() + result.getFieldError().getDefaultMessage(),
                ApiResponseCodes.INVALID_PARAMS
            );
        }

        authService.login(loginRequest, request);

        return new ResponseEntity<>(
            new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(), "Login success", null), HttpStatus.OK);

    }

    @PostMapping("logout")
    @Authenticate(module = AuthModule.AUTH_USER)
    @AuthUpgrade(module = AuthUpgradeModule.AUTH_USER, action = AuthAction.DELETE)
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String token = request.getAttribute(UtilsConstants.STR_CONST_AUTH_TOKEN).toString();
        authService.logout(token);
        return new ResponseEntity<>(
            new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(), "User logged in successfully", null),
            HttpStatus.OK);
    }

    @PostMapping("forgot-password/{email}")
    public ResponseEntity<ApiResponse<String>> forgotPassword(HttpServletRequest request,
        @PathVariable("email") String email) {

        if(!PatternUtils.isValidEmail(email)) {
            throw new ApiException(
                ApiResponseCodes.INVALID_PARAMS.getTitle(),
                ApiResponseCodes.INVALID_PARAMS.getMessage() + "Please provide correct email",
                ApiResponseCodes.INVALID_PARAMS
            );
        }

        authService.forgotPassword(email);

        return new ResponseEntity<>(new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(),"An email will be sent if used is logged in", null), HttpStatus.OK);
    }


    @GetMapping("get-user-data")
    @Authenticate(module = AuthModule.AUTH_USER)
    public ResponseEntity<ApiResponse<UserData>> getUserData(HttpServletRequest request) {

        String userId = request.getAttribute(UtilsConstants.STR_CONST_USER_DATA).toString();
        UserData  userData = authService.getUserData(userId);
        return new ResponseEntity<>(new ApiResponse<>(true,ApiResponseCodes.SUCCESS.getCode(),"User data fetched succesfully",
            userData), HttpStatus.OK);

    }

    @PostMapping("verify-user")
    @Authenticate(module = AuthModule.GUEST_USER)
    public ResponseEntity<ApiResponse<String>> verifyUser(HttpServletRequest request) {

        String email = request.getAttribute(UtilsConstants.STR_CONST_USER_EMAIL).toString();
        authService.verifyToken(email);
        return new ResponseEntity<>(new ApiResponse<>(true,ApiResponseCodes.SUCCESS.getCode(),"Email verified successfully, Please login",
            null), HttpStatus.OK);

    }

    @PostMapping("update-user-details")
    @Authenticate(module = AuthModule.AUTH_USER)
    @AuthUpgrade(module = AuthUpgradeModule.AUTH_USER, action = AuthAction.CREATE)
    public ResponseEntity<ApiResponse<String>> updateUserDetails(HttpServletRequest request,
        @Valid @RequestBody UpdateUserRequest updateUserRequest, BindingResult result) {

        log.info("Update user details process started");
        String userId = request.getAttribute(UtilsConstants.STR_CONST_USER_DATA).toString();

        if (result.hasErrors()) {
            throw new ApiException(
                ApiResponseCodes.INVALID_PARAMS.getTitle(),
                ApiResponseCodes.INVALID_PARAMS.getMessage() + result.getFieldError().getDefaultMessage(),
                ApiResponseCodes.INVALID_PARAMS
            );
        }

        updateUserRequest.setUserId(userId);

        authService.updateUserDetails(updateUserRequest, request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, ApiResponseCodes.SUCCESS.getCode(), "Data updated successfully", null),
            HttpStatus.OK);

    }


}
