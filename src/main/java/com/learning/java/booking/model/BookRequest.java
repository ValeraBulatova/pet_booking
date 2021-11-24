package com.learning.java.booking.model;

public class BookRequest {

    private final String roomName;
    private final int minutes;

    public BookRequest(String roomName, int minutes) {
        this.roomName = roomName;
        this.minutes = minutes;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMinutes() {
        return minutes;
    }
}
