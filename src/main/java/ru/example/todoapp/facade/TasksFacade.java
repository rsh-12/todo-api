package ru.example.todoapp.facade;

import ru.example.todoapp.util.filters.FilterByOperation;

import java.util.Set;

public interface TasksFacade {

    void addTasksToOrRemoveFromSection(Long sectionId, Set<Long> taskIds, FilterByOperation flag);

}
