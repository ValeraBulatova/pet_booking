package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Services {

    private final Map<String, Room> listRoom;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

    public String bookRoom(String roomName, int minutes) {
// TODO: 13.08.2021 check for minutes
        if(roomName == null) {
            return "";
        }

        Room room = listRoom.get(roomName);

        if(room == null){
            return "Invalid room name";
        }

        if (!room.isFree()){
            return String.format("Room %s is occupied", roomName);
        }

        executorService.schedule(() -> room.setFree(true), minutes, TimeUnit.MINUTES);

        Instant startTime = Instant.now();
        room.setBookTime(startTime);
        room.setFree(false);

        return String.format("Room %s is booked", roomName);
    }
}
