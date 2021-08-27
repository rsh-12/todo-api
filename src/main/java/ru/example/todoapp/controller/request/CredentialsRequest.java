package ru.example.todoapp.controller.request;
/*
 * Date: 29.07.2021
 * Time: 10:09 AM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.example.todoapp.validation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CredentialsRequest(@NotBlank
                                 @ValidEmail
                                 @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
                                 String username,

                                 @NotBlank
                                 @Size(min = 4, message = "Password is required")
                                 String password) {
}
