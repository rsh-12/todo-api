package ru.example.todo.service;

import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;

import java.util.Map;

/**
 * This interface contains authentication and
 * user management methods.
 */
public interface UserService {

    /**
     * Authenticates the user and returns access and refresh tokens.
     *
     * @param user contains username (email) and password
     * @param ip   the ip
     * @return access, refresh tokens, token type and expiration time in milliseconds
     * @throws CustomException if the user by username not found in the database
     */
    Map<String, String> login(User user, String ip);

    /**
     * Saves the user's name and password to the database, if the name is available,
     * otherwise throws the CustomException.
     *
     * @param user containes useraname (email) and password
     * @return the string <b>ok</b>
     * @throws CustomException if the username already in use
     */
    String register(User user);

    /**
     * Generates new access and refresh tokens if the old refresh token is valid.
     * Calls <b>buildResponseBody</b> method.
     *
     * @param refreshToken the refresh token
     * @param ip           the ip address
     * @return LinkedHashMap with tokens and some token properties
     */
    Map<String, String> generateNewTokens(String refreshToken, String ip);

    /**
     * Deletes the user by id if it exists in the database,
     * otherwise throws exception.
     *
     * @param userId the user id
     * @throws CustomException if the user does not exist
     */
    void deleteUserById(Long userId);

    /**
     * Finds the user by id if it exists in the database,
     * otherwise throws exception.
     *
     * @param userId the user id
     * @return the user
     * @throws CustomException if the user does not exist
     */
    User findUserById(Long userId);

    /**
     * Updates the user's password. Throws an exception, if user does not exists.
     *
     * @param email    the email as a string
     * @param password the new password as a string
     * @throws CustomException if the user by email not found
     */
    void updatePassword(String email, String password);

    /**
     * Checks whether a user with this username (email) exists.
     *
     * @param email the email
     * @return <b>true</b>, if user exists, otherwise <b>false</b>
     */
    boolean existsByUsername(String email);
}
