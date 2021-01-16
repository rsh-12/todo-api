package ru.example.todo.util;
/*
 * Date: 1/15/21
 * Time: 11:45 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todo.enums.TaskDate;

public class StringToDateEnumConverter implements Converter<String, TaskDate> {

    @Override
    public TaskDate convert(String string) {
        return TaskDate.valueOf(string.toUpperCase());
    }
}
