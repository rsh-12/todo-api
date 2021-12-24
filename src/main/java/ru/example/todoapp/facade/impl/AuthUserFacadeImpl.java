package ru.example.todoapp.facade.impl;
/*
 * Date: 23.07.2021
 * Time: 5:13 PM
 * */

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.example.todoapp.facade.AuthUserFacade;

@Component
public class AuthUserFacadeImpl implements AuthUserFacade {

    @Override
    public boolean containsRoleAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    @Override
    public Long getId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
