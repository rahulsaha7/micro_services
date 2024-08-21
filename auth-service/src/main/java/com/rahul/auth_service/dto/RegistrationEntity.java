package com.rahul.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegistrationEntity {
    private String id;
    private String userId;
    private String password;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Date createdOn;
    private Date updatedOn;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
