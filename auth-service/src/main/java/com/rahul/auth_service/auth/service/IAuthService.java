package com.rahul.auth_service.auth.service;

import com.rahul.auth_service.auth.model.LoginRequest;
import com.rahul.auth_service.auth.model.RegistrationModel;
import com.rahul.auth_service.auth.model.UpdateUserRequest;
import com.rahul.auth_service.auth.model.UserData;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {

        void register(RegistrationModel request);

        void login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

        void logout(String token);

        void forgotPassword(String email);

        UserData getUserData(String userId);

        void verifyToken(String email);

        UserData updateUserDetails(UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest);
}
