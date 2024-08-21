package com.rahul.auth_service.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rahul.auth_service.dto.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private UserRole role;


}
