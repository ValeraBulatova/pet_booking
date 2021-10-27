package com.learning.java.booking.model;


public class Room {

    private final String name;

    private final int id;

    private boolean free;

    public Room(String name, int id, boolean free) {
        this.name = name;
        this.id = id;
        this.free = free;
    }

    public String getName () {
        return name;
    }

    public boolean isFree () {
        return free;
    }

}
