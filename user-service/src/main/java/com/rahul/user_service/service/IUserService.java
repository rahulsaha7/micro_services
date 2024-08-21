package com.rahul.user_service.service;

import com.rahul.user_service.models.UpdateUserRequest;
import com.rahul.user_service.models.UserData;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {

    void registerUser(String userId);

    UserData getUserData(HttpServletRequest request);

    void updateUserDetails(UpdateUserRequest updateUserRequest);
}
