package ru.example.todo.util;
/*
 * Date: 1/17/21
 * Time: 8:22 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todo.enums.SetTasks;

public class StringToSetTasksEnumConverter implements Converter<String, SetTasks> {

    @Override
    public SetTasks convert(String string) {
        return SetTasks.valueOf(string.toUpperCase());
    }
}
