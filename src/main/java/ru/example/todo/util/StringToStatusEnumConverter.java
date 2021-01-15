package ru.example.todo.util;
/*
 * Date: 1/15/21
 * Time: 8:45 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todo.enums.TaskStatus;

public class StringToStatusEnumConverter implements Converter<String, TaskStatus> {

    @Override
    public TaskStatus convert(String string) {
        return TaskStatus.valueOf(string.toUpperCase());
    }
}
