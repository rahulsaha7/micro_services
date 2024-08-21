package com.rahul.auth_service.dto;

import com.rahul.auth_service.auth.model.UserData;
import com.rahul.auth_service.auth.repository.IAuthRepository;
import com.rahul.auth_service.utils.CookieUtils;
import com.rahul.auth_service.utils.JsonUtils;
import com.rahul.auth_service.utils.TokenUtils;
import com.rahul.auth_service.utils.UtilsConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AuthInterceptor implements HandlerInterceptor {

    private IAuthRepository authRepository;
    private IBlacklistedTokensRepository blacklistedTokensRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        boolean isValidUser = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Authenticate authenticate = handlerMethod.getMethodAnnotation(Authenticate.class);
            if (authenticate != null) {
                log.info("authenticate module is present , {}", authenticate);
                switch (authenticate.module()) {
                    case AUTH_USER:
                        return handleUserAuthenticate(request, response);
                    case GUEST_USER:
                        return handleGuestUserAuthenticate(request, response);

                }
            }
        }
        return true;
    }

    private boolean handleGuestUserAuthenticate(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String token = extractTokenFromHeader(request, UtilsConstants.STR_CONST_AUTH_USER);
        if(StringUtils.isBlank(token)) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized, Token must be present"));
            return false;
        }
        String email = TokenUtils.getDataFromToken(token);
        if (StringUtils.isBlank(email)) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized, you are not a user of us"));
            return false;
        }

        Optional<RegistrationEntity> oUser = authRepository.getUserDataByEmail(email);
        if(oUser.isEmpty()) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized, you are not a user of us"));
            return false;
        }
        RegistrationEntity user = oUser.get();
        request.setAttribute(UtilsConstants.STR_CONST_USER_EMAIL, user.getEmail());
        return true;
    }

    private String extractTokenFromHeader(HttpServletRequest request, String strConstAuthUser) {
        String authorizationHeader = request.getHeader("Authorization");

        // Check if the Authorization header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token by removing the "Bearer " prefix
            return authorizationHeader.substring(7); // The token starts after "Bearer "
        }

        // Return null or throw an exception if the token is not found
        return null;
    };

    private boolean handleUserAuthenticate(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String token = extractTokenFromCookie(request, UtilsConstants.STR_CONST_AUTH_USER);
        if(token == null) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized"));
            return false;
        }
        Optional<String> oDbTokenData = blacklistedTokensRepository.isBlacklisted(token);

        if(oDbTokenData.isPresent()) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized"));
            return false;
        }

        String userDataJson = TokenUtils.getDataFromToken(token);
        if (userDataJson == null) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized"));
            return false;
        }
        UserData userData = JsonUtils.getJsonObject(userDataJson, UserData.class);
        if (userData == null) {
            sendFailureResponse(response, getAuthFailureResponse("Something unexpected happened"));
            return false;
        }
        Optional<RegistrationEntity> oUser = authRepository.getUserDataByEmail(userData.getEmail());
        if(oUser.isEmpty()) {
            sendFailureResponse(response, getAuthFailureResponse("Unauthorized"));
            return false;
        }
        RegistrationEntity user = oUser.get();
        if(UserStatus.BLOCKED.equals(user.getStatus())) {
            sendFailureResponse(response, getAuthFailureResponse("User is blocked"));
            return false;
        }
        request.setAttribute(UtilsConstants.STR_CONST_USER_DATA, user.getUserId());
        request.setAttribute(UtilsConstants.STR_CONST_AUTH_TOKEN, token);
        return true;
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        return CookieUtils.getCookieValue(request, cookieName);
    }

    private void sendFailureResponse(HttpServletResponse response,
        ApiResponse authenticationFailureResponse) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type", "application/json");
        response.getWriter().write(
            Objects.requireNonNull(JsonUtils.getJsonString(authenticationFailureResponse)));
    }

    private ApiResponse getAuthFailureResponse(String message) {
        return new ApiResponse<>(false, ApiResponseCodes.UNAUTHORIZED_ACCESS.getCode(),
            ApiResponseCodes.UNAUTHORIZED_ACCESS.getMessage() + message,
            null);
    }


}
