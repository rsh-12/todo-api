package ru.example.todo.facade;
/*
 * Date: 7/1/21
 * Time: 7:44 AM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.impl.PasswordFacadeImpl;
import ru.example.todo.messaging.MessagingClient;
import ru.example.todo.messaging.request.TokenRequest;
import ru.example.todo.service.UserService;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasswordFacadeTest {

    @InjectMocks
    private PasswordFacadeImpl passwordFacade;

    @Mock
    private MessagingClient messagingService;

    @Mock
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

        assertThrows(CustomException.class, () ->
                passwordFacade.updatePassword(new TokenRequest("someToken"), "somePassword"));

        verify(messagingService).sendTokenAndReceiveEmail(any());
        verifyNoInteractions(userService);
    }

}
