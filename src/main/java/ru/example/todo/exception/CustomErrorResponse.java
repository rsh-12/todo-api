package ru.example.todo.exception;
/*
 * Date: 1/14/21
 * Time: 10:54 AM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class CustomErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Yekaterinburg")
    private Date timestamp;
    private int status;
    private String error;
    private String message;

    public CustomErrorResponse() {
    }

    public CustomErrorResponse(Date timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
