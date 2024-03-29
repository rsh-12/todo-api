package ru.example.todoapp.domain.request;
/*
 * Date: 5/29/21
 * Time: 10:39 PM
 * */

public class TokenRequest {

    private String token;

    public TokenRequest() {
    }

    public TokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                '}';
    }
}
