package ru.example.todo.facade;
/*
 * Date: 23.07.2021
 * Time: 5:11 PM
 * */

import ru.example.todo.entity.User;

public interface AuthUserFacade {

    User getPrincipal();

}
