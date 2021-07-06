package ru.example.todo.actuator;
/*
 * Date: 06.07.2021
 * Time: 8:05 AM
 * */

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import ru.example.todo.repository.TodoTaskRepository;

@Component
@Endpoint(id = "task-stats")
public class TaskStatsEndpoint {

    private final TodoTaskRepository taskRepository;

    public TaskStatsEndpoint(TodoTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @ReadOperation
    public Stats stats() {
        return new Stats(taskRepository.count(), taskRepository.countByCompleted(true));
    }

    private static class Stats {
        private long count;
        private long completed;

        public Stats(long count, long completed) {
            this.count = count;
            this.completed = completed;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public long getCompleted() {
            return completed;
        }

        public void setCompleted(long completed) {
            this.completed = completed;
        }
    }

}

