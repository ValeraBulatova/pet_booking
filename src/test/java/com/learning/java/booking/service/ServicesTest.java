package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ServicesTest {


    JdbcService jdbcService;
    Services services;


    ServicesTest() {
        jdbcService = mock(JdbcService.class);
        services = new Services(jdbcService);
    }

    @Test
    void testGetRoomStatus() {
        Assertions.assertEquals("Please, input the room name", services.getRoomStatus(null));
    }

    @Test
    void testBookRoomRoomNameNull() {
        Assertions.assertEquals("Please input the room name", services.bookRoom(null, 20));
    }

    @Test
    void testBookRoomInvalidRoomName() {
        Assertions.assertEquals("Invalid room name", services.bookRoom("test", 20));
    }

    @Test
    void testBookRoomValidRoomName() {
        when(jdbcService.geAllRooms()).thenReturn(new HashMap<>(){{
            put("V", new Room("V", 1, true));
        }});
        Assertions.assertEquals("Room V is booked", services.bookRoom("V", 20));
    }

    @Test
    void testBookOccupiedRoom() {
        when(jdbcService.geAllRooms()).thenReturn(new HashMap<>(){{
            put("V", new Room("V", 1, false));
        }});
        Assertions.assertEquals("Room V is occupied", services.bookRoom("V", 20));
    }

    @Test
    void bookRoomTimeIsMoreThanAllowed() {
        Assertions.assertEquals("Maximum allowed time for booking is 2 hours", services.bookRoom("V", 121));
    }

    @Test
    void bookRoomLessTimeThanAllowed() {
        Assertions.assertEquals("Minimum allowed time for booking is 15 minutes", services.bookRoom("V", 14));
    }
}
