package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import com.learning.java.booking.model.RoomResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BookingService {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final JpaService jpaService;

    public BookingService(JpaService jpaService) {
        this.jpaService = jpaService;
    }

    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public RoomResponse getRoomStatus(String name) {
        if (StringUtils.isEmpty(name)) {
            return new RoomResponse("Please, input the room name");
        }

        Room room = jpaService.getRoom(name);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", name));
            return new RoomResponse(String.format("Room %s was not found", name));
        }

        String status = room.isOccupied() ? "occupied" : "free";
        return new RoomResponse(String.format("Room %s is %s", room.getName(), status));
    }

    public RoomResponse bookRoom(String roomName, int minutes) {

        if (minutes > 120) {
            return new RoomResponse("Maximum allowed time for booking is 2 hours");
        } else if (minutes < 15) {
            return new RoomResponse("Minimum allowed time for booking is 15 minutes");
        }
        if (StringUtils.isEmpty(roomName)) {
            return new RoomResponse("Please input the room name");
        }

        Room room = jpaService.getRoom(roomName);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            return new RoomResponse("Invalid room name");
        }

        if (room.isOccupied()) {
            return new RoomResponse(String.format("Room %s is occupied", roomName));
        }

        executorService.schedule(() -> jpaService.updateRoomStatus(roomName, 0, 0 ), minutes, TimeUnit.MINUTES);

        Instant now = Instant.now();

        long timeCalculation = now.getEpochSecond();

        Integer bookStart = Math.toIntExact(timeCalculation);
        Integer bookEnd = Math.toIntExact(timeCalculation + TimeUnit.MINUTES.toSeconds(minutes));
        jpaService.updateRoomStatus(roomName, bookStart, bookEnd);

        String message = jpaService.getRoom(roomName).isOccupied() ? String.format("Room %s is booked", roomName)
                : String.format("Room %s is NOT booked due to internal error", roomName);

        return new RoomResponse(message);
    }
}
