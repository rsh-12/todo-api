package ru.example.todo.model;
/*
 * Date: 6/5/21
 * Time: 8:09 AM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todo.messaging.requests.EmailRequest;

import static org.junit.Assert.assertEquals;

public class EmailRequestTest {

    private EmailRequest emailRequest;
    private final String EMAIL = "user@mail.com";

    @Before
    public void setUp() throws Exception {
        emailRequest = new EmailRequest();
        emailRequest.setEmail(EMAIL);
    }

    @Test
    public void getEmail_ShouldReturnEmail() {
        assertEquals(EMAIL, emailRequest.getEmail());
    }

}
