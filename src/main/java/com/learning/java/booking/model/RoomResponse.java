package com.learning.java.booking.model;


import org.springframework.http.HttpStatus;

public class RoomResponse {

    private String message;
    private String code;

    public RoomResponse(String message, HttpStatus code) {
        this.message = message;
        this.code = String.valueOf(code);
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

}
