package com.rahul.auth_service.auth.model;

import com.rahul.auth_service.utils.PatternUtils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "Email cannot be empty")
    @Pattern(regexp = PatternUtils.EMAIL_PATTERN, message = "Invalid email")
    private String email;

    @NotNull(message = "Password cannot be empty")
    private String password;
}
