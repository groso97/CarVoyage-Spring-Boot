package com.project.CarVoyage.classes.user;

public interface UserRepository {
    User findByUsername(String username);

    User findByEmail(String email);

    void saveUser(User user);

    User findByConfirmationToken(String token);

    void updateEmailVerification(User user);

    void updatePassword(User user);

    void deleteByUsername(String username);
}
