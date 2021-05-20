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
    @Size(min = 6, max = 6)
    private String code;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 255, message = "Password is required")
    private String password;

    public PasswordRequest() {
    }

    public PasswordRequest(String username, String code, String password) {
        this.username = username;
        this.code = code;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PasswordRequest{" +
                "username='" + username + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
