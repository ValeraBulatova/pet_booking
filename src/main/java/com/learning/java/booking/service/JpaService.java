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

    void updateRoomStatus(String roomName, Integer startBook, Integer endBook) {

        Room room = roomRepository.findByName(roomName);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
        }
        boolean statusToUpdate = !room.isOccupied();
        room.setOccupied(statusToUpdate);
        room.setBookStart(startBook);
        room.setBookEnd(endBook);
        roomRepository.save(room);
    }
}
