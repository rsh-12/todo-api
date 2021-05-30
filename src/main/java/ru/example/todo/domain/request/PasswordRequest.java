package ru.example.todo.domain.request;
/*
 * Date: 5/20/21
 * Time: 9:48 AM
 * */

import javax.validation.constraints.*;
import java.util.Objects;

public class PasswordRequest {

    @NotNull
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\.(ru|com)",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Not a valid email address")
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 255, message = "Password is required")
    private String password;

    public PasswordRequest() {
    }

    public PasswordRequest(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordRequest that = (PasswordRequest) o;

        if (!Objects.equals(username, that.username)) return false;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PasswordRequest{" +
                "username='" + username + '\'' +
                '}';
    }

}
