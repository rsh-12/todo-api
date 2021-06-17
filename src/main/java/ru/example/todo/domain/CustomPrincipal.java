package ru.example.todo.domain;
/*
 * Date: 6/17/21
 * Time: 4:43 PM
 * */

import ru.example.todo.enums.Role;

import java.security.Principal;
import java.util.Set;

public class CustomPrincipal implements Principal {

    private final Long id;
    private final String name;
    private final Set<Role> roles;

    public CustomPrincipal(Long id, String name, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<Role> getRoles() {
        return roles;
    }


}
