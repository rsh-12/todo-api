package ru.example.todo.util.converters;
/*
 * Date: 1/15/21
 * Time: 8:45 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todo.enums.filters.FilterByBoolean;

public class StringToStatusEnumConverter implements Converter<String, FilterByBoolean> {

    @Override
    public FilterByBoolean convert(String string) {
        return FilterByBoolean.valueOf(string.toUpperCase());
    }
}
