package ru.example.todo.aop;
/*
 * Date: 13.07.2021
 * Time: 7:32 PM
 * */

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoTaskRepository;

@Aspect
@Component
public class ValidatingAspect {

    private final TodoTaskRepository taskRepository;

    public ValidatingAspect(TodoTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Pointcut("execution(* ru.example.todo.service.impl.TodoTaskServiceImpl.deleteTaskById(..))")
    private void deleteTask() {
    }


    @Before(value = "deleteTask() && args(principal, taskId)", argNames = "principal,taskId")
    public void validateTaskDeleting(User principal, Long taskId) {
        TodoTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> CustomException.notFound("Task not found: id=" + taskId));
        equalsOrHasRoleAdmin(principal, task.getUser());
    }

    private void equalsOrHasRoleAdmin(User principal, User user) {
        boolean isValid = (user != null && user.equals(principal)) || principal.getRoles().contains(Role.ADMIN);
        if (!isValid) {
            throw CustomException.forbidden("Not enough permissions");
        }
    }

}
