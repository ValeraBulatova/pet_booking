package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;

import java.util.Optional;

public interface DAO {
    Optional<Room> getRoom(String name);
    boolean updateRoomStatus(String roomName, long startBookingSeconds, long endBookingSeconds);
}
