package com.bluehospital.patient.patient.dto;

public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private String path;
    private T data;

    public ApiResponse(int statusCode,String message, String path, T data){
        this.statusCode=statusCode;
        this.message=message;
        this.path=path;
        this.data=data;
    }

    //Getters and Setters


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
