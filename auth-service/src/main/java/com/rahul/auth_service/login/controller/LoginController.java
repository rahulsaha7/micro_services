package com.rahul.auth_service.login.controller;

import com.rahul.auth_service.user_service_client.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/")
public class LoginController {

    @PostMapping("login")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("Login successful", org.springframework.http.HttpStatus.OK);

    }

}
