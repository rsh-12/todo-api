package ru.example.todoapp.domain.request;
/*
 * Date: 12.08.2021
 * Time: 9:57 AM
 * */

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordRequest {

    @NotBlank
    @Size(min = 4, message = "Password is required")
    private String password;

    public PasswordRequest() {
    }

    public PasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
