package ru.example.todoapp.util.converters;
/*
 * Date: 1/15/21
 * Time: 11:45 AM
 * */

import org.springframework.core.convert.converter.Converter;
import ru.example.todoapp.util.filters.FilterByDate;

public class StringToDateEnumConverter implements Converter<String, FilterByDate> {

    @Override
    public FilterByDate convert(String string) {
        try {
            return FilterByDate.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return FilterByDate.ALL;
        }
    }

}
