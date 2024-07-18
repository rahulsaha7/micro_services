package com.rahul.auth_service.user_service_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface IUserService {
    @GetMapping("user/status/check")
    public ResponseEntity<String> status();
}
