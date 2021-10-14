package ru.example.todoapp.facade;

import java.util.Set;

public interface TasksFacade {

    void addTasks(Long sectionId, Set<Long> taskIds);

    void removeTasks(Long sectionId, Set<Long> taskIds);
}
