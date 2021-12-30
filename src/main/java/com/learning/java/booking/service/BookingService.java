package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String getRoomStatus(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Please, input the room name");
        }

        Room room = jpaService.getRoom(name);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", name));
            throw new NoSuchElementException(String.format("Room %s was not found in database", name));
        }

        String status = room.isOccupied() ? "occupied" : "free";
        return String.format("Room %s is %s", room.getName(), status);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean bookRoom(String roomName, int minutes) {

        if (minutes > 120) {
            throw new IllegalArgumentException("Maximum allowed time for booking is 2 hours");
        } else if (minutes < 1) {
            throw new IllegalArgumentException("Minimum allowed time for booking is 15 minutes");
        }
        if (StringUtils.isEmpty(roomName)) {
            throw new IllegalArgumentException("Room name is empty");
        }

        Room room = jpaService.getRoom(roomName);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            throw new NoSuchElementException(String.format("Room %s was not found in database", roomName));
        }

        if (room.isOccupied()) {
            return false;
        }

        Instant now = Instant.now();

        long bookEnd = now.getEpochSecond() + TimeUnit.MINUTES.toSeconds(minutes);

        boolean booked = jpaService.updateRoomStatus(room, now.getEpochSecond(), bookEnd);
        executorService.schedule(() -> jpaService.updateRoomStatus(roomName, 0, 0 ), minutes, TimeUnit.MINUTES);

        return booked;
    }
}
