package com.learning.java.booking.service;


import com.learning.java.booking.rooms.Room;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Services {

    private final Map<String, Room> listRoom;

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
            throw new IllegalArgumentException("name is null");
        }

        Room room = listRoom.get(name);
        if (room == null) {
            return "not found";
        }

        String status = room.isFree() ? "free" : "occupied";
        return String.format("Room %s is %s", room.getName(), status);
    }
}
