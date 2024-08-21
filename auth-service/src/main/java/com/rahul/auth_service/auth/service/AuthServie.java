package com.rahul.auth_service.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rahul.auth_service.auth.model.LoginRequest;
import com.rahul.auth_service.auth.model.RegistrationModel;
import com.rahul.auth_service.auth.model.UpdateUserRequest;
import com.rahul.auth_service.auth.model.UserData;
import com.rahul.auth_service.auth.model.VerifyEmailSendPojo;
import com.rahul.auth_service.auth.model.VerifyEmailSendPojo.Payload;
import com.rahul.auth_service.auth.repository.IAuthRepository;
import com.rahul.auth_service.dto.ApiException;
import com.rahul.auth_service.dto.ApiResponseCodes;
import com.rahul.auth_service.dto.IBlacklistedTokensRepository;
import com.rahul.auth_service.dto.RegistrationEntity;
import com.rahul.auth_service.dto.UserRole;
import com.rahul.auth_service.dto.UserStatus;
import com.rahul.auth_service.producers.IKafkaProducer;
import com.rahul.auth_service.utils.JsonUtils;
import com.rahul.auth_service.utils.TokenUtils;
import com.rahul.auth_service.utils.UtilsConstants;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AuthServie implements IAuthService{

    private IAuthRepository authRepository;
    private IKafkaProducer kafkaProducer;
    private IBlacklistedTokensRepository tokensRepository;

    @Override
    public void register(RegistrationModel request) {
        validateRequest(request.getEmail(), request.getMobile());
        String userId = UUID.randomUUID().toString();
        authRepository.register(request, userId);

        kafkaProducer.emitUserRegistrationEvent("USER-REGISTRATION",
            getUserRegistrationEventRequest(request, userId));
        kafkaProducer.emitEmailInitiationEvent("REGISTRATION",
            getUserDataForRegistrationEmail(request.getEmail(), request.getFirstName(), request.getLastName(),
                "http://localhost.8065/login"
            ));

    }

    private String getUserDataForRegistrationEmail(String email, String firstName, String lastName, String loginUrl) {
        VerifyEmailSendPojo emailSendPojo = new VerifyEmailSendPojo(
            email,
            new Payload(
                firstName.concat(" ").concat(lastName),
                loginUrl
            )
        );
        return JsonUtils.getJsonString(emailSendPojo);
    }

    @Override
    public void login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {

        Optional<RegistrationEntity> oUserDataByEmail = authRepository.getUserDataByEmail(loginRequest.getEmail());
        if (oUserDataByEmail.isEmpty()) {
            throw new ApiException(ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS.getTitle(),
                ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS.getMessage(),
                ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS);
        }
        RegistrationEntity userData = oUserDataByEmail.get();
        if (!userData.getPassword().equals(loginRequest.getPassword())) {
            throw new ApiException(ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS.getTitle(),
                ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS.getMessage(),
                ApiResponseCodes.LOGIN_FAILED_INVALID_CREDENTIALS);
        }

        if(UserStatus.INACTIVE.equals(userData.getStatus())){
           // Active it and then login
        }

        if(UserStatus.BLOCKED.equals(userData.getStatus())){
            throw new ApiException(ApiResponseCodes.LOGIN_FAILED_ACCOUNT_BLOCKED.getTitle(),
                ApiResponseCodes.LOGIN_FAILED_ACCOUNT_BLOCKED.getMessage(),
                ApiResponseCodes.LOGIN_FAILED_ACCOUNT_BLOCKED);
        }

        UserData user = new UserData(userData.getFirstName(), userData.getLastName(), userData.getEmail(),
            userData.getPhoneNumber(), null, userData.getRole());

        String token = null;
        try {
            token = TokenUtils.generateToken(JsonUtils.getJsonString(user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        httpServletRequest.setAttribute(UtilsConstants.STR_CONST_AUTH_TOKEN , token);

        if(UserRole.GUEST.equals(userData.getRole())){
            String verifyEmailToken = null;
            String payloadInJson = null;
            try {
                verifyEmailToken = TokenUtils.generateToken(userData.getEmail());
                payloadInJson =  getVerifyEmailData(userData.getFirstName(), userData.getLastName(), userData.getEmail(), verifyEmailToken);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            kafkaProducer.emitEmailInitiationEvent("VERIFY-EMAIL", payloadInJson);
        }

    }

    private String getVerifyEmailData(String firstName, String lastName, String email, String token) {
        // Todo : Get the base url from properties file
        VerifyEmailSendPojo pojo = new VerifyEmailSendPojo(
            email,
            new Payload(
                firstName.concat(" ").concat(lastName),
                "http://localhost:8065/verify-user/".concat(token)
            )
        );
        return JsonUtils.getJsonString(pojo);
    }

    @Override
    public void logout(String token) {
        tokensRepository.addTokenToBlacklist(token);
    }

    @Override
    public void forgotPassword(String email) {
        Optional<RegistrationEntity> oUserDataByEmail = authRepository.getUserDataByEmail(email);
        if(oUserDataByEmail.isPresent()){
            // create token data
            // send json object as payload having email and token
            kafkaProducer.emitEmailInitiationEvent("FORGOT-PASSWORD", email);
        }
    }

    @Override
    public UserData getUserData(String userId) {
        Optional<RegistrationEntity> oUserData = authRepository.getUserdataByUserId(userId);
        if (oUserData.isEmpty()) {
            throw new ApiException(ApiResponseCodes.USER_NOT_FOUND.name(), "User not found", ApiResponseCodes.USER_NOT_FOUND);
        }
        RegistrationEntity userData = oUserData.get();
        return new UserData(userData.getFirstName(), userData.getLastName(), userData.getEmail(), userData.getPhoneNumber(), null, userData.getRole());
    }

    @Override
    public void verifyToken(String email) {
        RegistrationEntity userData = authRepository.updateUserRoleByUserId(UserRole.USER, email);
        VerifyEmailSendPojo pojo = new VerifyEmailSendPojo(
            email,
            new Payload(
                userData.getFirstName().concat(" ").concat(userData.getLastName()),
                null
            )
        );
        kafkaProducer.emitEmailInitiationEvent("VERIFIED", JsonUtils.getJsonString(pojo));
    }

    @Override
    public UserData updateUserDetails(UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest) {
        validateRequest(null,updateUserRequest.getMobile());
        RegistrationEntity userDataFromDb = authRepository.update(updateUserRequest);
        UserData user = new UserData(userDataFromDb.getFirstName(), userDataFromDb.getLastName(), userDataFromDb.getEmail(),
            userDataFromDb.getPhoneNumber(), null, userDataFromDb.getRole());
        String token = null;
        try {
            token = TokenUtils.generateToken(JsonUtils.getJsonString(user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        httpServletRequest.setAttribute(UtilsConstants.STR_CONST_AUTH_TOKEN , token);
        return user;
    }

    private String getUserRegistrationEventRequest(RegistrationModel request, String userId) {
        try {
            // Values to be added to the JSON

            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Create an ObjectNode to build the JSON
            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("user_id", userId);
            jsonObject.put("profilephoto", request.getProfilePhoto());
            jsonObject.put("email", request.getEmail());
            jsonObject.put("mobileNumber", request.getMobile());
            jsonObject.put("firstName", request.getFirstName());
            jsonObject.put("lastName", request.getLastName());

            // Convert ObjectNode to JSON String
            String jsonString = objectMapper.writeValueAsString(jsonObject);

            return jsonString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void validateRequest(String email, String mobile) {
        if (StringUtils.isNotBlank(email)) {
            Optional<RegistrationEntity> oAuthUserData = authRepository.getUserDataByEmail(email);
            if (oAuthUserData.isPresent()) {
                throw new ApiException(
                    ApiResponseCodes.DUPLICATE_EMAIL.getTitle(),
                    ApiResponseCodes.DUPLICATE_EMAIL.getMessage(),
                    ApiResponseCodes.DUPLICATE_EMAIL
                );
            }
        }
        if (StringUtils.isNotBlank(mobile)) {
            Optional<RegistrationEntity> oAuthUserDataByMobile = authRepository.getUserDataByMobileNumber(mobile);
            if (oAuthUserDataByMobile.isPresent()) {
                throw new ApiException(
                    ApiResponseCodes.DUPLICATE_MOBILE_NUMBER.getTitle(),
                    ApiResponseCodes.DUPLICATE_MOBILE_NUMBER.getMessage(),
                    ApiResponseCodes.DUPLICATE_MOBILE_NUMBER
                );
            }
        }
    }
}
