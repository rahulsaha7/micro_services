package com.rahul.auth_service.dto;

import com.rahul.auth_service.utils.CookieUtils;
import com.rahul.auth_service.utils.UtilsConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class SessionAuthService {

    public void handleResponse(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
        AuthUpgrade authUpgrade) {

        switch (authUpgrade.action()) {
            case UPGRADE :
                break;
            case CREATE:
                createCookie(servletRequest, servletResponse, authUpgrade);
                break;
            case DELETE:
                removeCookie(servletRequest, servletResponse, authUpgrade);
                break;
        }

    }

    private void removeCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse, AuthUpgrade authUpgrade) {
        AuthUpgradeModule module = authUpgrade.module();

        switch (module) {
            case AUTH_USER:
                CookieUtils.deleteCookie(
                    servletResponse,
                    UtilsConstants.STR_CONST_AUTH_USER
                );
                break;
        }
    }

    private void createCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse, AuthUpgrade authUpgrade) {
        AuthUpgradeModule module = authUpgrade.module();
        switch (module) {
            case AUTH_USER:
                String authToken = servletRequest.getAttribute(UtilsConstants.STR_CONST_AUTH_TOKEN).toString();
                CookieUtils.createCookie(
                    servletResponse,
                    UtilsConstants.STR_CONST_AUTH_USER,
                    authToken,
                    86400,
                    true
                );
                break;
        }
    }
}
