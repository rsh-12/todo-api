package ru.example.todo.messaging.request;
/*
 * Date: 6/5/21
 * Time: 8:09 AM
 * */

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmailRequestTest {

    private EmailRequest emailRequest;
    private final String EMAIL = "user@mail.com";

    @Before
    public void setUp() {
        emailRequest = new EmailRequest();
        emailRequest.setEmail(EMAIL);
    }

    @Test
    public void getEmail_ShouldReturnEmail() {
        assertEquals(EMAIL, emailRequest.getEmail());
    }

    @Test
    public void setEmail_ShouldSetEmail() {
        emailRequest.setEmail("admin@mail.com");
        assertEquals("admin@mail.com", emailRequest.getEmail());
    }

    @Test
    public void toString_ShouldCheckOverridedMethod() {
        assertEquals(emailRequest.toString(), "Email{email='" + emailRequest.getEmail() + "'}");
    }

}
