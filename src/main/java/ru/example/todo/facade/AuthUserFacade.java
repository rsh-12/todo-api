package ru.example.todo.facade;
/*
 * Date: 23.07.2021
 * Time: 5:11 PM
 * */

import ru.example.todo.domain.CustomPrincipal;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;

import java.util.Set;

public interface AuthUserFacade {


    /**
     * <b>This method returns the User that contains onle id and roles parsed from access token!</b>
     */
    User getLoggedUser();

    CustomPrincipal getPrincipal();

    User mapToUser(CustomPrincipal principal);

    CustomPrincipal mapToPrincipal(User user);

    Long getUserId();

    Set<Role> getUserRoles();

}
