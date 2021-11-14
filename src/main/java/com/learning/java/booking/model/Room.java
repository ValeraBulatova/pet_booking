package com.learning.java.booking.model;


public class Room {
    private final String name;
    private final int id;
    private final boolean occupied;

    public Room(String name, int id, boolean occupied) {
        this.name = name;
        this.id = id;
        this.occupied = occupied;
    }

    public String getName () {
        return name;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getId() {
        return id;
    }
}
