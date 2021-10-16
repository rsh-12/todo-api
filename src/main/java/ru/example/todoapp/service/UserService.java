package ru.example.todoapp.service;

import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;

import java.util.Optional;

/**
 * This interface contains authentication and
 * user management methods.
 */
public interface UserService {

    /**
     * Deletes the user by id if it exists in the database,
     * otherwise throws exception.
     *
     * @param userId the user id
     * @throws CustomException if the user does not exist
     */
    void delete(Long userId);

    /**
     * Finds the user by id if it exists in the database,
     * otherwise throws exception.
     *
     * @param userId the user id
     * @return the user
     * @throws CustomException if the user does not exist
     */
    Optional<User> findOne(Long userId);

    /**
     * Updates the user's password. Throws an exception, if user does not exists.
     *
     * @param email    the email as a string
     * @param password the new password as a string
     * @return Optional of user
     * @throws CustomException if the user by email not found
     */
    Optional<User> updatePassword(String email, String password);

    /**
     * Checks whether a user with this username (email) exists.
     *
     * @param email the email
     * @return <b>true</b>, if user exists, otherwise <b>false</b>
     */
    boolean existsByUsername(String email);

}
