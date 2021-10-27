package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;

import java.util.Map;

public interface DAO {
    Room getRoom(String name);
    Map<String, Room> getAllRooms();
    boolean updateRoomStatus(String roomName, long startBookingSeconds, long endBookingSeconds);
}
