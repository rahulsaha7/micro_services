package com.rahul.auth_service.dto;

import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@Getter
@Setter
public class BlacklistedTokenRepository implements IBlacklistedTokensRepository{

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String INSERT_USER = """
            INSERT INTO blacklisted_token (token,  created_date)
            VALUES (:token, :created_date)
        """;

    private static final String GET_TOKEN_DATA = """
            SELECT token from blacklisted_token where token = :token 
        """;

    @Override
    public void addTokenToBlacklist(String token) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("token", token);
        paramSource.addValue("created_date", new Date());
        jdbcTemplate.update(INSERT_USER, paramSource);
    }

    @Override
    public Optional<String> isBlacklisted(String token) {

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("token", token);
        try {
            return Optional.of(jdbcTemplate.queryForObject(GET_TOKEN_DATA, paramSource, String.class));
        }catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }

    }

}
