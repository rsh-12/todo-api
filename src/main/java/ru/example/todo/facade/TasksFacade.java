package ru.example.todo.facade;

import ru.example.todo.enums.filters.FilterByOperation;

import java.util.Set;

public interface TasksFacade {

    void addTasksToOrRemoveFromSection(Long userId, Long sectionId, Set<Long> taskIds, FilterByOperation flag);

}
