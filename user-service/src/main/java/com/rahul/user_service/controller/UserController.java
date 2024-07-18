package com.rahul.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/status/check")
    public ResponseEntity<String> status() {
        return new ResponseEntity<>("User Service is working", HttpStatus.OK);
    }

}
