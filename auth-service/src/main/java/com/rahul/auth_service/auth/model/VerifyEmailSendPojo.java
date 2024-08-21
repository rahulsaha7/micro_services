package com.rahul.auth_service.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class VerifyEmailSendPojo {
    private String senderEmailId;
    private Payload payload;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        private String username;
        private String payload;
    }
}
