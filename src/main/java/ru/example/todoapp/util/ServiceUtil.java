package ru.example.todoapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.example.todoapp.facade.AuthUserFacade;

@Component
public class ServiceUtil {

    private final AuthUserFacade currentUser;

    @Autowired
    public ServiceUtil(AuthUserFacade currentUser) {
        this.currentUser = currentUser;
    }

    public boolean ownerOrAdmin(Long userId) {
        return currentUser.getId().equals(userId) || currentUser.containsRoleAdmin();
    }

}
