package ru.example.todoapp.facade.impl;
/*
 * Date: 23.07.2021
 * Time: 5:13 PM
 * */

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.example.todoapp.domain.CustomPrincipal;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.domain.Role;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.security.UserDetailsImpl;

import java.util.Set;

@Component
public class AuthUserFacadeImpl implements AuthUserFacade {

    private final ModelMapper modelMapper;

    public AuthUserFacadeImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User getLoggedUser() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return principal.getUser();
    }

    @Override
    public CustomPrincipal getPrincipal() {
        return modelMapper.map(getLoggedUser(), CustomPrincipal.class);
    }

    @Override
    public User mapToUser(CustomPrincipal principal) {
        return modelMapper.map(principal, User.class);
    }

    @Override
    public CustomPrincipal mapToPrincipal(User user) {
        return modelMapper.map(user, CustomPrincipal.class);
    }

    @Override
    public Long getUserId() {
        return getLoggedUser().getId();
    }

    @Override
    public Set<Role> getUserRoles() {
        return getLoggedUser().getRoles();
    }

}
