package ru.example.todoapp.facade;
/*
 * Date: 7/1/21
 * Time: 7:44 AM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.controller.request.TokenRequest;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.impl.PasswordFacadeImpl;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.service.UserService;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
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
        given(userService.updatePassword(anyString(), anyString()))
                .willReturn(Optional.of(new User("username@mail.com", "somePassword")));

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
