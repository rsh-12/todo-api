package ru.example.todo.facade;
/*
 * Date: 7/1/21
 * Time: 7:44 AM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.exception.CustomException;
import ru.example.todo.messaging.MessagingService;
import ru.example.todo.messaging.requests.TokenRequest;
import ru.example.todo.service.UserService;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordFacadeTest {

    @Autowired
    private PasswordFacade passwordFacade;

    @MockBean
    private MessagingService messagingService;

    @MockBean
    private UserService userService;

    @Test
    public void updatePassword_ShouldCallServiceMethods() {
        given(messagingService.sendTokenAndReceiveEmail(any())).willReturn("user@mail.com");
        doNothing().when(userService).updatePassword(anyString(), anyString());

        passwordFacade.updatePassword(new TokenRequest("someToken"), "somePassword");

        verify(messagingService).sendTokenAndReceiveEmail(any());
        verify(userService).updatePassword(anyString(), anyString());
    }

    @Test
    public void updatePassword_ShouldThrowCustomException() {
        given(messagingService.sendTokenAndReceiveEmail(any())).willReturn("");
        doNothing().when(userService).updatePassword(anyString(), anyString());

        assertThrows(CustomException.class, () ->
                passwordFacade.updatePassword(new TokenRequest("someToken"), "somePassword"));

        verify(messagingService).sendTokenAndReceiveEmail(any());
        verifyNoInteractions(userService);
    }

}
