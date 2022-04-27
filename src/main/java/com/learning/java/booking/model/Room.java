package com.learning.java.booking.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Room")
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "occupied")
    private boolean occupied;
    @Column(name = "book_start")
    private long bookStart;
    @Column(name = "book_end")
    private long bookEnd;

    @Autowired
    public Room() {}

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

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void setBookStart(long bookStart) {
        this.bookStart = bookStart;
    }

    public void setBookEnd(long bookEnd) {
        this.bookEnd = bookEnd;
    }
}
