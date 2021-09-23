package ru.example.todoapp.util.filters;

public enum FilterByOperation {
    MOVE("move"),
    REMOVE("remove");

    private final String VALUE;

    FilterByOperation(String value) {
        VALUE = value;
    }

    public String VALUE() {
        return VALUE;
    }
}
