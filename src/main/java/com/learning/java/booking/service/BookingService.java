package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        if (StringUtils.isEmpty(name)) {
            return "Please, input the room name";
        }

        Optional<Room> room = jdbcService.getRoom(name);
        if (!room.isPresent()) {
            LOGGER.info(String.format("Room %s was not found in database", name));
            return "not found";
        }

        String status = room.get().isOccupied() ? "occupied" : "free";
        return String.format("Room %s is %s", room.get().getName(), status);
    }

    public String bookRoom(String roomName, int minutes) {

        if (minutes > 120) {
            return "Maximum allowed time for booking is 2 hours";
        } else if (minutes < 15) {
            return "Minimum allowed time for booking is 15 minutes";
        }
        if (StringUtils.isEmpty(roomName)) {
            return "Please input the room name";
        }

        Optional<Room> optionalRoom = jdbcService.getRoom(roomName);
        if (!optionalRoom.isPresent()) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            return "Invalid room name";
        }
        Room room = optionalRoom.get();

        if (room.isOccupied()) {
            return String.format("Room %s is occupied", roomName);
        }

        executorService.schedule(() -> jdbcService.updateRoomStatus(roomName, 0, 0 ), minutes, TimeUnit.MINUTES);

        Instant now = Instant.now();

        long bookStart = now.getEpochSecond();
        long bookEnd = bookStart + TimeUnit.MINUTES.toSeconds(minutes);
        boolean wasBooked = jdbcService.updateRoomStatus(roomName, bookStart, bookEnd);

        return wasBooked ? String.format("Room %s is booked", roomName) : String.format("Room %s is NOT booked due to internal error", roomName);
    }
}
