package com.learning.java.booking.service;


import org.springframework.stereotype.Service;
import com.learning.java.booking.rooms.Room;

import java.util.Map;

@Service
public class Services {

    private Map<String, Room> listRoom;


    public Services(Map<String, Room> listRoom) {
        this.listRoom = listRoom;
    }


    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public String getRoomStatus(String name) {
        if (name == null) {
            return "wrong room name";
        }
        Room room = listRoom.get(name);
        if (room == null) {
            return "Room is not found";
        }
        String isFree = room.isFree() ? " is free" : " is occupied";
        return "Room " + room.getName() + isFree;
    }
}
