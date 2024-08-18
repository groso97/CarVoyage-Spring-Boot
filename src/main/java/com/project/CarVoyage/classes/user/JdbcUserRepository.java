package com.project.CarVoyage.classes.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JdbcUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT user_id, first_name, last_name, email, username, password, created_at, email_verified, confirmation_token FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return users.isEmpty() ? null : users.get(0);
    }



    @Override
    public User findByEmail(String email) {
        String sql = "SELECT user_id, first_name, last_name, email, username, password, created_at, email_verified, confirmation_token FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), email);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public void saveUser(User user) {
        String sql = """
                INSERT INTO users (first_name, last_name, email, username, password, email_verified, confirmation_token) \
                VALUES (?, ?, ?, ?, ?, ?, ?)\
                """;
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername(),
                user.getPassword(), user.isEmailVerified(),
                user.getConfirmationToken());

    }

    @Override
    public User findByConfirmationToken(String token) {
        String sql = """
                SELECT user_id, first_name, last_name, email, username, password, created_at, email_verified, confirmation_token \
                FROM users WHERE confirmation_token = ?\
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), token);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void updateEmailVerification(User user) {
        String sql = "UPDATE users SET email_verified = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.isEmailVerified(), user.getUserId());
    }

    @Override
    public void updatePassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getUserId());
    }
}
