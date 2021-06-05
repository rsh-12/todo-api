package ru.example.todo.facade;

import ru.example.todo.enums.SetTasks;

import java.util.Set;

public interface TasksFacade {

    void addTasksToOrRemoveFromSection(Long userId, Long sectionId, Set<Long> taskIds, SetTasks flag);

}
