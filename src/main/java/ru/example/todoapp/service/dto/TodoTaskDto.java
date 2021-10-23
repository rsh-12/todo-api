package ru.example.todoapp.service.dto;
/*
 * Date: 3/12/21
 * Time: 6:52 PM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Relation(value = "task", collectionRelation = "tasks")
public record TodoTaskDto(

        @JsonIgnore
        Long id,
        String title,
        LocalDate completionDate,
        boolean completed,
        boolean starred,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
