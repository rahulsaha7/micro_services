package com.rahul.user_service.service;

import com.rahul.user_service.models.UpdateUserRequest;
import com.rahul.user_service.models.UserData;
import dto.enums.ApiResponseCodes;
import dto.exceptions.ApiException;
import dto.responses.ApiResponse;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserService implements IUserService{

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private IAuthService authService;

    @Override
    public void registerUser(String userId) {

    }

    @Override
    public UserData getUserData(HttpServletRequest request) {
        String id = "id";
        try {
            ApiResponse<UserData> getUserDataBody = authService.getUserData().getBody();
            if(!getUserDataBody.isSuccess()) {
                ApiResponseCodes actualError = ApiResponseCodes.getByCode(getUserDataBody.getCode());
                throw new ApiException(actualError.getTitle(), actualError.getMessage(), actualError);
            }
            return getUserDataBody.getData();
        }catch (FeignException ex) {
            log.error("Something unexpected happened while calling the getting user-data, Exception : ",
                ex.getMessage());
            if (HttpStatus.UNAUTHORIZED.value() == ex.status()) {
                throw new ApiException(
                    ApiResponseCodes.UNAUTHORIZED_ACCESS.getTitle(),
                    ApiResponseCodes.UNAUTHORIZED_ACCESS.getMessage(),
                    ApiResponseCodes.UNAUTHORIZED_ACCESS
                );
            }
            throw new ApiException(
                ApiResponseCodes.SERVER_ERROR.getTitle(),
                ApiResponseCodes.SERVER_ERROR.getMessage(),
                ApiResponseCodes.SERVER_ERROR
            );
        }catch (Exception e) {
            log.error("Something unexpected happened while calling the getting user-data, Exception : ", e);
            throw new ApiException(
                ApiResponseCodes.SERVER_ERROR.getTitle(),
                ApiResponseCodes.SERVER_ERROR.getMessage(),
                ApiResponseCodes.SERVER_ERROR
            );
        }
    }

    @Override
    public void updateUserDetails(UpdateUserRequest updateUserRequest) {
        try {
            ApiResponse<String> getUserDataBody = authService.updateUserDetails(updateUserRequest).getBody();
            if(!getUserDataBody.isSuccess()) {
                ApiResponseCodes actualError = ApiResponseCodes.getByCode(getUserDataBody.getCode());
                throw new ApiException(actualError.getTitle(), actualError.getMessage(), actualError);
            }
        }catch (FeignException ex) {
            log.error("Something unexpected happened while calling the getting user-data, Exception : ",
                ex.getMessage());
            if (HttpStatus.UNAUTHORIZED.value() == ex.status()) {
                throw new ApiException(
                    ApiResponseCodes.UNAUTHORIZED_ACCESS.getTitle(),
                    ApiResponseCodes.UNAUTHORIZED_ACCESS.getMessage(),
                    ApiResponseCodes.UNAUTHORIZED_ACCESS
                );
            }
            throw new ApiException(
                ApiResponseCodes.SERVER_ERROR.getTitle(),
                ApiResponseCodes.SERVER_ERROR.getMessage(),
                ApiResponseCodes.SERVER_ERROR
            );
        }catch (Exception e) {
            log.error("Something unexpected happened while calling the getting user-data, Exception : ", e);
            throw new ApiException(
                ApiResponseCodes.SERVER_ERROR.getTitle(),
                ApiResponseCodes.SERVER_ERROR.getMessage(),
                ApiResponseCodes.SERVER_ERROR
            );
        }
    }
}
