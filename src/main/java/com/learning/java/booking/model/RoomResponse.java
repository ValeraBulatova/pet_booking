package com.learning.java.booking.model;


public class RoomResponse {

    private String message;
    private int code;

    public RoomResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
