package com.rahul.auth_service.dto;

import java.util.Optional;

public interface IBlacklistedTokensRepository {


    void addTokenToBlacklist(String token);

    Optional<String> isBlacklisted(String token);
}
