package ru.example.todoapp.dsl;
/*
 * Date: 18.08.2021
 * Time: 10:11 AM
 * */

import ru.example.todoapp.entity.User;
import ru.example.todoapp.domain.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class UserBuilder {

    private final User user = new User();

    public static User user(Consumer<UserBuilder> consumer) {
        UserBuilder builder = new UserBuilder();
        consumer.accept(builder);

        return builder.user;
    }

    public void username(String username) {
        user.setUsername(username);
    }

    public void password(String password) {
        user.setPassword(password);
    }

    public void roles(Consumer<RoleBuilder> consumer) {
        RoleBuilder builder = new RoleBuilder();
        consumer.accept(builder);

        user.setRoles(builder.roles);
    }

    public static class RoleBuilder {

        private final Set<Role> roles = new HashSet<>();

        public void role(Role role) {
            roles.add(role);
        }
    }

}

