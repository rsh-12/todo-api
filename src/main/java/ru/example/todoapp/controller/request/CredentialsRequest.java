package ru.example.todoapp.controller.request;
/*
 * Date: 29.07.2021
 * Time: 10:09 AM
 * */

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record CredentialsRequest(@NotBlank
                                 @Pattern(regexp = "^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\.(ru|com)",
                                         flags = Pattern.Flag.CASE_INSENSITIVE,
                                         message = "Not a valid email address")
                                 @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
                                 String username,

                                 @NotBlank
                                 @Size(min = 4, message = "Password is required")
                                 String password) {
}
