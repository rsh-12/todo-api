package ru.example.todo.domain;
/*
 * Date: 23.07.2021
 * Time: 9:38 PM
 * */

import ru.example.todo.enums.Role;

import java.util.Objects;
import java.util.Set;

public class CustomPrincipal {

    private Long id;
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public CustomPrincipal() {
    }

    public CustomPrincipal(Long id, Set<Role> roles) {
        this.id = id;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomPrincipal that = (CustomPrincipal) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserPrincipal{id=" + id + ", roles=" + roles + '}';
    }

}
