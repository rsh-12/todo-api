package ru.example.todoapp.service.mapper;
/*
 * Date: 23.10.2021
 * Time: 7:42 AM
 * */

import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.service.dto.TodoTaskDto;

@Service
public class TaskMapper {

    public TodoTaskDto mapToTaskDto(TodoTask task) {
        if (task == null) {
            return null;
        }

        return new TodoTaskDto(
                task.getId(),
                task.getTitle(),
                task.getCompletionDate(),
                task.isCompleted(),
                task.isStarred(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

}
