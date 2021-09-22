package ru.example.todoapp.util.converters;
/*
 * Date: 1/17/21
 * Time: 8:22 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todoapp.util.filters.FilterByOperation;

public class StringToSetTasksEnumConverter implements Converter<String, FilterByOperation> {

    @Override
    public FilterByOperation convert(String string) {
        return FilterByOperation.valueOf(string.toUpperCase());
    }
}
