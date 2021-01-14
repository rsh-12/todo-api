package ru.example.todo.exception;
/*
 * Date: 1/14/21
 * Time: 10:54 AM
 * */

public class CustomErrorResponse {

    private int status;
    private String message;
    private long timestamp;

    public CustomErrorResponse() {
    }

    public CustomErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
