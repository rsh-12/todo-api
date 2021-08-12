package ru.example.todoapp.messaging.request;
/*
 * Date: 6/4/21
 * Time: 2:23 PM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todoapp.controller.request.TokenRequest;

import static org.junit.Assert.assertEquals;

public class TokenRequestTest {

    private TokenRequest tokenRequest;
    private final String TOKEN = "sometoken";

    @Before
    public void setUp() {
        tokenRequest = new TokenRequest();
        tokenRequest.setToken(TOKEN);
    }

    @Test
    public void getToken_ShouldReturnToken() {
        assertEquals(TOKEN, tokenRequest.getToken());
    }

    @Test
    public void setToken_ShouldUpdateToken() {
        tokenRequest.setToken("newToken");
        assertEquals("newToken", tokenRequest.getToken());
    }

    @Test
    public void toString_ShouldCheckOverridedMethod() {
        assertEquals(tokenRequest.toString(), "Token{token='"+tokenRequest.getToken()+"'}");
    }

}
