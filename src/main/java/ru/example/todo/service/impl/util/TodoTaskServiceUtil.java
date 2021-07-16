package ru.example.todo.service.impl.util;
/*
 * Date: 15.07.2021
 * Time: 9:22 PM
 * */

import org.springframework.data.domain.Sort;

public final class TodoTaskServiceUtil {

    private TodoTaskServiceUtil() {
    }

    public static Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    public static String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

}
