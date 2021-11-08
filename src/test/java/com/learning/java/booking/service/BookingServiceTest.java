package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceTest {


    private final JdbcService jdbcService;
    private final BookingService bookingService;


    BookingServiceTest() {
        jdbcService = mock(JdbcService.class);
        bookingService = new BookingService(jdbcService);
    }

    @Test
    void testGetRoomStatus() {
        Assertions.assertEquals("Please, input the room name", bookingService.getRoomStatus(null));
    }

    @Test
    void testBookRoomRoomNameNull() {
        Assertions.assertEquals("Please input the room name", bookingService.bookRoom(null, 20));
    }

    @Test
    void testBookRoomInvalidRoomName() {
        Assertions.assertEquals("Invalid room name", bookingService.bookRoom("test", 20));
    }

    @Test
    void testBookRoomValidRoomName() {
        when(jdbcService.getAllRooms()).thenReturn(new HashMap<String, Room>(){{
            put("V", new Room("V", 1, true));
        }});
        when(jdbcService.updateRoomStatus("V", 0, 0)).thenReturn(true);
        Assertions.assertEquals("Room V is booked", bookingService.bookRoom("V", 20));
    }

    @Test
    void testBookOccupiedRoom() {
        when(jdbcService.getAllRooms()).thenReturn(new HashMap<String, Room>(){{
            put("V", new Room("V", 1, false));
        }});
        Assertions.assertEquals("Room V is occupied", bookingService.bookRoom("V", 20));
    }

    @Test
    void bookRoomTimeIsMoreThanAllowed() {
        Assertions.assertEquals("Maximum allowed time for booking is 2 hours", bookingService.bookRoom("V", 121));
    }

    @Test
    void bookRoomLessTimeThanAllowed() {
        Assertions.assertEquals("Minimum allowed time for booking is 15 minutes", bookingService.bookRoom("V", 14));
    }
}
