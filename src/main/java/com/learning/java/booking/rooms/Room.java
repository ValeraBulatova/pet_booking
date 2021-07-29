package com.learning.java.booking.rooms;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

//@Component("room") read docs
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Room {

    private final String name;

    private final int id;

    private final boolean free;

    private LocalTime time;

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

    public int getId(){
        return id;
    }

}
