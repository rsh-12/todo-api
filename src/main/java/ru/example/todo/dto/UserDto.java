package ru.example.todo.dto;
/*
 * Date: 3/25/21
 * Time: 10:24 AM
 * */

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {

    @NotBlank
    @Pattern(regexp = "^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\.(ru|com)",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Not a valid email address")
    @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 4, message = "Password is required")
    private String password;

    public UserDto() {
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (username != null ? !username.equals(userDto.username) : userDto.username != null) return false;
        return password != null ? password.equals(userDto.password) : userDto.password == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

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
