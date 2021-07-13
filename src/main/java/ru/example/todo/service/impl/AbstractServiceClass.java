package ru.example.todo.service.impl;
/*
 * Date: 5/8/21
 * Time: 7:21 AM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.filters.FilterByOperation;

import java.util.List;

public class AbstractServiceClass {

    private static final Logger log = LoggerFactory.getLogger(AbstractServiceClass.class.getName());

    Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

    void addOrRemoveTasks(FilterByOperation flag, TodoSection section, List<TodoTask> tasksByIds) {

        if (flag.equals(FilterByOperation.MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasksByIds);
        } else if (flag.equals(FilterByOperation.REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasksByIds);
        }

    }

}
