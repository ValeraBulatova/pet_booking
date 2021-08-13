package com.learning.java.booking.model;

import java.time.Instant;


public class Room {

    private final String name;

    private final int id;

    private boolean free;

    private Instant bookTime;

    private int bookFor;

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

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setBookTime(Instant bookTime) {
        this.bookTime = bookTime;
    }

    public int getId(){
        return id;
    }

}
