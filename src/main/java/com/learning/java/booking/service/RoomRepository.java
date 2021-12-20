package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomRepository extends JpaRepository<Room, String> {

    Room findByName(String name);
}
