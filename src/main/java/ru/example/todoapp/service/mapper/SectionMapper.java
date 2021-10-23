package ru.example.todoapp.service.mapper;
/*
 * Date: 23.10.2021
 * Time: 7:39 AM
 * */

import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.service.dto.TodoSectionDto;

@Service
public class SectionMapper {

    public TodoSectionDto mapToSectionDto(TodoSection section) {
        if (section == null) {
            return null;
        }
        return new TodoSectionDto(section.getId(),
                section.getTitle(),
                section.getUpdatedAt(),
                section.getCreatedAt());
    }

}
