package com.learning.java.booking.service;


import org.springframework.stereotype.Service;
import com.learning.java.booking.rooms.Room;

import java.util.HashMap;

@Service
public class Services {

    private HashMap<String, Room> listRoom = new HashMap<>();

    public Services() {
        listRoom.put("A", new Room("A", 1, true));
        listRoom.put("B", new Room("B", 2, false));
        listRoom.put("C", new Room("C", 3, true));
    }


    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public String roomStatus(String name) {
        String isFree = " is occupied";
        if(listRoom.get(name).isFree()) isFree = " is free";
        return "Room " + listRoom.get(name).getName() + isFree;
    }



}
