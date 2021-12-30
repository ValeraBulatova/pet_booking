package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class JpaService {

    private final Logger LOGGER = LoggerFactory.getLogger(JpaService.class);

    private final RoomRepository roomRepository;

    public JpaService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    Room getRoom(String name) {
        return roomRepository.findByName(name);
    }

    boolean updateRoomStatus(String roomName, long startBook, long endBook) {

        Room room = roomRepository.findByName(roomName);
        if (room == null) {
            LOGGER.error(String.format("Room %s was not found in database", roomName));
            return false;
        }
        return updateWithRetry(room, startBook, endBook, 0);
    }

    boolean updateRoomStatus(Room room, long startBook, long endBook) {
        return updateWithRetry(room, startBook, endBook, 0);
    }

    private boolean updateWithRetry(Room room, long startBook, long endBook, int counter) {
        boolean statusToUpdate = !room.isOccupied();
        room.setOccupied(statusToUpdate);
        room.setBookStart(startBook);
        room.setBookEnd(endBook);
        try {
            roomRepository.save(room);
            return true;
        } catch (IllegalArgumentException e) {
            if (counter < 2) {
                counter++;
                return updateWithRetry(room, startBook, endBook, counter);
            }
            LOGGER.error("Connection to database failed");
            return false;
        }
    }
}
