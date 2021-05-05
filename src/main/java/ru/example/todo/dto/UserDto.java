package ru.example.todo.dto;
/*
 * Date: 3/25/21
 * Time: 10:24 AM
 * */

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {

    @Pattern(regexp = "^[a-z]{2,}@[a-z]{2,5}\\.(ru|com)",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Not a valid email address")
    @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
    @NotBlank
    @NotEmpty
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 4, message = "Password is required")
    @NotBlank
    @NotEmpty
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
