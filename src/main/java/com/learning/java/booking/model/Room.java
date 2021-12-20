package com.learning.java.booking.model;

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
    private Integer bookStart;
    @Column(name = "book_end")
    private Integer bookEnd;

    public Room() {}

    public String getName () {
        return name;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void setBookStart(Integer bookStart) {
        this.bookStart = bookStart;
    }

    public void setBookEnd(Integer bookEnd) {
        this.bookEnd = bookEnd;
    }
}
