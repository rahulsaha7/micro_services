package com.rahul.auth_service.auth.repository;

import com.rahul.auth_service.auth.model.RegistrationModel;
import com.rahul.auth_service.auth.model.UpdateUserRequest;
import com.rahul.auth_service.dto.RegistrationEntity;
import com.rahul.auth_service.dto.UserRole;
import java.util.Optional;

public interface IAuthRepository {
    void register(RegistrationModel request, String userId);

    Optional<RegistrationEntity> getUserDataByEmail(String email);

    Optional<RegistrationEntity> getUserDataByMobileNumber(String mobile);

    Optional<RegistrationEntity> getUserdataByUserId(String userId);

    RegistrationEntity update(UpdateUserRequest updateUserRequest);

    RegistrationEntity updateUserRoleByUserId(UserRole userRole, String userId);
}
