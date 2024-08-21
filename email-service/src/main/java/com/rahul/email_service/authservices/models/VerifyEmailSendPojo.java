package com.rahul.email_service.authservices.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailSendPojo {

    @JsonProperty("senderEmailId")
    private String senderEmailId;
    @JsonProperty("payload")
    private Payload payload;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("username")
        private String username;
        @JsonProperty("payload")
        private String payload;
    }
}
