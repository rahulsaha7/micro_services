package com.rahul.auth_service.auth.repository;

import com.rahul.auth_service.auth.model.RegistrationModel;
import com.rahul.auth_service.auth.model.UpdateUserRequest;
import com.rahul.auth_service.dto.RegistrationEntity;
import com.rahul.auth_service.dto.UserRole;
import com.rahul.auth_service.dto.UserStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository implements IAuthRepository{

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String GET_USER_DATA_BY_EMAIL = """
            SELECT * FROM auth_user WHERE email = :email 
        """;

    private static final String GET_USER_DATA_BY_MOBILE = """
            SELECT * FROM auth_user WHERE mobile_number = :mobile 
        """;

    private static final String GET_USER_DATA_BY_USER_ID = """
            SELECT * FROM auth_user WHERE user_id = :userId 
        """;

    private static final String INSERT_USER = """
            INSERT INTO auth_user (email, mobile_number, first_name, last_name, user_id, password, role, status, created_on, updated_on)
            VALUES (:email, :mobile_number, :first_name, :last_name,  :userId, :password, :role, :status, :createdOn, :updatedOn )
      
        """;

    private static final String UPDATE_USER = """
            UPDATE auth_user
            set 
            mobile_number = COALESCE(:mobileNumber, mobile_number),
            password = COALESCE(:password, password),
            first_name = COALESCE(:firstName, first_name),
            last_name = COALESCE(:lastName, last_name)
            where user_id = :userId
            RETURNING *
        """;

    private static final String UPDATE_USER_ROLE = """
            UPDATE auth_user
            set 
            role = :userRole
            where email = :email
            returning *
        """;


    @Override
    public void register(RegistrationModel request, String userId) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        paramSource.addValue("password", request.getPassword());
        paramSource.addValue("role", UserRole.GUEST.name());
        paramSource.addValue("status", UserStatus.ACTIVE.name());
        paramSource.addValue("createdOn", new Date());
        paramSource.addValue("updatedOn", new Date());
        paramSource.addValue("email", request.getEmail());
        paramSource.addValue("mobile_number", request.getMobile());
        paramSource.addValue("first_name", request.getFirstName());
        paramSource.addValue("last_name", request.getLastName());
        jdbcTemplate.update(INSERT_USER, paramSource);
    }

    @Override
    public Optional<RegistrationEntity> getUserDataByEmail(String email) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("email", email);
        try {
           return Optional.of(jdbcTemplate.queryForObject(GET_USER_DATA_BY_EMAIL, paramSource, new DataMapper()));
        }catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RegistrationEntity> getUserDataByMobileNumber(String mobile) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("mobile", mobile);
        try {
            return Optional.of(jdbcTemplate.queryForObject(GET_USER_DATA_BY_MOBILE, paramSource, new DataMapper()));
        }catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RegistrationEntity> getUserdataByUserId(String userId) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", userId);
        try {
            return Optional.of(jdbcTemplate.queryForObject(GET_USER_DATA_BY_USER_ID, paramSource, new DataMapper()));
        }catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public RegistrationEntity update(UpdateUserRequest updateUserRequest) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("userId", updateUserRequest.getUserId());
        paramSource.addValue("password", updateUserRequest.getPassword());
        paramSource.addValue("mobileNumber", updateUserRequest.getMobile());
        paramSource.addValue("firstName", updateUserRequest.getFirstName());
        paramSource.addValue("lastName", updateUserRequest.getLastName());

        return  jdbcTemplate.queryForObject(UPDATE_USER, paramSource, new DataMapper());
    }

    @Override
    public RegistrationEntity updateUserRoleByUserId(UserRole userRole, String email) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("email", email);
        paramSource.addValue("userRole", userRole.name());
        return jdbcTemplate.queryForObject(UPDATE_USER_ROLE, paramSource, new DataMapper());
    }

    private static class DataMapper implements RowMapper<RegistrationEntity> {

        @Override
        public RegistrationEntity mapRow(ResultSet resultSet, int i) throws SQLException {
            RegistrationEntity entity = new RegistrationEntity();
            entity.setId(resultSet.getString("id"));
            entity.setUserId(resultSet.getString("user_id"));
            entity.setEmail(resultSet.getString("email"));
            entity.setPassword(resultSet.getString("password"));
            entity.setRole(UserRole.valueOf(resultSet.getString("role")));
            entity.setStatus(UserStatus.valueOf(resultSet.getString("status")));
            entity.setCreatedOn(resultSet.getDate("created_on"));
            entity.setUpdatedOn(resultSet.getDate("updated_on"));
            entity.setFirstName(resultSet.getString("first_name"));
            entity.setLastName(resultSet.getString("last_name"));
            entity.setPhoneNumber(resultSet.getString("mobile_number"));
            return entity;
        }
    }
}
