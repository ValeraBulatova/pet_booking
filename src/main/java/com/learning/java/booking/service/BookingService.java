package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// TODO: 27.10.2021 tests
@Service
public class BookingService {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final JdbcService jdbcService;

    public BookingService(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public String getRoomStatus(String name) {

        Map<String, Room> rooms = jdbcService.geAllRooms();

        if (name == null) {
            return "Please, input the room name";
        }

        Room room = rooms.get(name);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", name));
            return "not found";
        }

        String status = room.isFree() ? "free" : "occupied";
        return String.format("Room %s is %s", room.getName(), status);
    }

    public String getStatusOfAllRooms() {

        Map<String, Room> rooms = jdbcService.geAllRooms();

        List<String> roomNames = new ArrayList<>(rooms.keySet());

        String allStatuses = "";

        for (String name : roomNames) {
            allStatuses = allStatuses + getRoomStatus(name) + "\r\n";
        }
        return allStatuses;
    }

    public String bookRoom(String roomName, int minutes) {

        if (minutes > 120) {
            return "Maximum allowed time for booking is 2 hours";
        } else if (minutes < 15) {
            return "Minimum allowed time for booking is 15 minutes";
        }
        if (StringUtils.isEmpty(StringUtils.trim(roomName))) {
            return "Please input the room name";
        }

        Map<String, Room> rooms = jdbcService.geAllRooms();
        Room room = rooms.get(roomName);

        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            return "Invalid room name";
        }

        if (!room.isFree()) {
            return String.format("Room %s is occupied", roomName);
        }

        executorService.schedule(() -> jdbcService.unbookRoomInDataBase(roomName), minutes, TimeUnit.MINUTES);

        Instant startTime = Instant.now();

        long startTimeSeconds = startTime.getEpochSecond();
        long bookForSeconds = startTimeSeconds + TimeUnit.MINUTES.toSeconds(minutes);
        boolean wasBooked = jdbcService.bookRoomInDataBase(roomName, startTimeSeconds, bookForSeconds);

        return wasBooked ? String.format("Room %s is booked", roomName) : String.format("Room %s is NOT booked due to internal error", roomName);
    }
}
