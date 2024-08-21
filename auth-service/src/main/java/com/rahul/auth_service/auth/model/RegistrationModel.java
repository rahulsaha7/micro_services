package com.rahul.auth_service.auth.model;

import com.rahul.auth_service.utils.PatternUtils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationModel {

    @NotNull(message = "mobile field can't not be empty")
    @Pattern(regexp = PatternUtils.PHONE_PATTERN, message = "Invalid phone number format")
    private String mobile;


    @NotNull(message = "Email field can't not be empty")
    @Pattern(regexp = PatternUtils.EMAIL_PATTERN, message = "Invalid email format")
    private String email;

    @NotNull(message = "password filed can't be empty")
    @Pattern(regexp = PatternUtils.PASSWORD_PATTERN, message = "Password must be 8 to 15 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Pattern(regexp = PatternUtils.BASE64_PATTERN, message = "Invalid profile photo format")
    private String profilePhoto;

    @NotNull
    @Pattern(regexp = PatternUtils.NAME_PATTERN, message = "First name must contain only alphabetic characters")
    private String firstName;

    @NotNull
    @Pattern(regexp = PatternUtils.NAME_PATTERN, message = "Last name must contain only alphabetic characters")
    private String lastName;


}
