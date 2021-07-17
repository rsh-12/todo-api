package ru.example.todo.service.impl.util;
/*
 * Date: 18.07.2021
 * Time: 12:33 AM
 * */

import org.springframework.data.domain.Sort;

public record TaskServiceUtil() {

    public static Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    public static String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

}
