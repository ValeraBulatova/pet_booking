package com.learning.java.booking.model;



public class RoomStatus {

    private final String message;
    private final int code;

    public RoomStatus(String message, int code) {
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
