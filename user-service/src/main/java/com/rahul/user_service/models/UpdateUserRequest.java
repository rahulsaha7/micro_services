package com.rahul.user_service.models;

import com.rahul.user_service.Utils.PatternUtils;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UpdateUserRequest {


    @Pattern(regexp = PatternUtils.PHONE_PATTERN, message = "Invalid phone number format")
    private String mobile;

    @Pattern(regexp = PatternUtils.PASSWORD_PATTERN, message = "Password must be 8 to 15 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Pattern(regexp = PatternUtils.BASE64_PATTERN, message = "Invalid profile photo format")
    private String profilePhoto;

    @Pattern(regexp = PatternUtils.NAME_PATTERN, message = "First name must contain only alphabetic characters")
    private String firstName;

    @Pattern(regexp = PatternUtils.NAME_PATTERN, message = "Last name must contain only alphabetic characters")
    private String lastName;

    @Setter
    private String userId;

}
