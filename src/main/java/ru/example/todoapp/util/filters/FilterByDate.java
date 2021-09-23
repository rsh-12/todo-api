package ru.example.todoapp.util.filters;

public enum FilterByDate {
    TODAY("today"),
    OVERDUE("overdue"),
    ALL("all");

    private final String VALUE;

    FilterByDate(String value) {
        VALUE = value;
    }

    public String VALUE() {
        return VALUE;
    }
}
