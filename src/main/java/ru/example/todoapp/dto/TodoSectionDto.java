package ru.example.todoapp.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Relation(value = "section", collectionRelation = "sections")
public record TodoSectionDto(@JsonIgnore Long id, String title, LocalDateTime updatedAt, LocalDateTime createdAt) {

}
