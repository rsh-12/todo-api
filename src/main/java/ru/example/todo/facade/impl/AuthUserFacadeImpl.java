package ru.example.todo.facade.impl;
/*
 * Date: 23.07.2021
 * Time: 5:13 PM
 * */

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.example.todo.entity.User;
import ru.example.todo.facade.AuthUserFacade;
import ru.example.todo.security.UserDetailsImpl;

@Component
public class AuthUserFacadeImpl implements AuthUserFacade {

    @Override
    public User getPrincipal() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return principal.getUser();
    }
}
