package ru.example.todo.aop;
/*
 * Date: 13.07.2021
 * Time: 7:32 PM
 * */

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.repository.TodoTaskRepository;

@Aspect
@Component
public class ValidatingAspect {

    private final TodoTaskRepository taskRepository;
    private final TodoSectionRepository sectionRepository;

    public ValidatingAspect(TodoTaskRepository taskRepository, TodoSectionRepository sectionRepository) {
        this.taskRepository = taskRepository;
        this.sectionRepository = sectionRepository;
    }

    @Pointcut("execution(* ru.example.todo.service.impl.TodoTaskServiceImpl.deleteTaskById(..))")
    private void deleteTask() {
    }

    @Pointcut("execution(* ru.example.todo.service.impl.TodoSectionServiceImpl.deleteSectionById(..))")
    private void deleteSection() {
    }

    @Pointcut("execution(* ru.example.todo.service.impl.TodoSectionServiceImpl.updateSection(..))")
    private void updateSection() {
    }

    @Before(value = "updateSection() && args(principal, sectionId, todoSection))",
            argNames = "principal,sectionId,todoSection")
    public void validateSectionUpdating(User principal, Long sectionId, TodoSection todoSection) {
        validateSectionOps(principal, sectionId);
    }

    @Before(value = "deleteSection() && args(principal, sectionId))", argNames = "principal,sectionId")
    public void validateSectionDeleting(User principal, Long sectionId) {
        validateSectionOps(principal, sectionId);
    }

    private void validateSectionOps(User principal, Long sectionId) {
        TodoSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Section Not Found", HttpStatus.NOT_FOUND));
        equalsOrHasRoleAdmin(principal, section.getUser());
    }

    @Before(value = "deleteTask() && args(principal, taskId)", argNames = "principal,taskId")
    public void validateTaskDeleting(User principal, Long taskId) {
        TodoTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Task Not Found", HttpStatus.NOT_FOUND));
        equalsOrHasRoleAdmin(principal, task.getUser());
    }

    private void equalsOrHasRoleAdmin(User principal, User user) {
        boolean isValid = (user != null && user.equals(principal)) || principal.getRoles().contains(Role.ADMIN);
        if (!isValid) {
            throw new CustomException("Not enough permissions", HttpStatus.FORBIDDEN);
        }
    }

}
