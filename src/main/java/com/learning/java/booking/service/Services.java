package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Services {

    private Map<String, Room> rooms;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private JdbcService jdbcService;

    public Services(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
        this.rooms = jdbcService.geAllRooms();
    }

    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public String getRoomStatus(String name) {

        rooms = jdbcService.geAllRooms();

        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        Room room = rooms.get(name);
        if (room == null) {
            return "not found";
        }

        String status = room.isFree() ? "free" : "occupied";
        return String.format("Room %s is %s", room.getName(), status);
    }

    public String getStatusOfAllRooms() {

        List<String> roomNames = new ArrayList<>(rooms.keySet());

        String allStatuses = "";

        for (String name : roomNames) {
            allStatuses = allStatuses + getRoomStatus(name) + "\r\n";
        }
        return allStatuses;
    }

    public String bookRoom(String roomName, int minutes) {

        if(minutes > 120) {
            return "Maximum allowed startTimeSeconds is 2 hours";
        }
        if(roomName == null) {
            return "Please input the room name";
        }

        rooms = jdbcService.geAllRooms();
        Room room = rooms.get(roomName);

        if(room == null){
            return "Invalid room name";
        }

        if (!room.isFree()){
            return String.format("Room %s is occupied", roomName);
        }

        executorService.schedule(() -> jdbcService.unbookRoomInDataBase(roomName), minutes, TimeUnit.MINUTES);

        Instant startTime = Instant.now();

        long startTimeSeconds = startTime.getEpochSecond();
        long bookForSeconds = startTimeSeconds + TimeUnit.MINUTES.toSeconds(minutes);
        jdbcService.bookRoomInDataBase(roomName, startTimeSeconds, bookForSeconds);

        return String.format("Room %s is booked", roomName);
    }
}
