package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: 16.11.2021 use parameterized tests
class BookingServiceTest {


    private final JdbcService jdbcService;
    private final BookingService bookingService;


    BookingServiceTest() {
        jdbcService = mock(JdbcService.class);
        bookingService = new BookingService(jdbcService);
    }

    @Test
    @DisplayName("Room status is not shown, return message with reason, if roomName == null")
    void getRoomStatus_roomNameIsNull_messageInvalidRoomName() {
        Assertions.assertEquals("Please, input the room name", bookingService.getRoomStatus(null));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if roomName == null")
    void bookRoom_roomNameIsNull_messageInvalidRoomName() {
        Assertions.assertEquals("Please input the room name", bookingService.bookRoom(null, 20));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if there is no room with required roomName")
    void bookRoom_invalidRoomName_messageInvalidRoomName() {
        Assertions.assertEquals("Invalid room name", bookingService.bookRoom("test", 20));
    }

    @Test
    @DisplayName("Room is booked, if values are valid and room is not occupied")
    void bookRoom_validEntryValues_roomIsBooked() {
        when(jdbcService.getRoom("V")).thenReturn(Optional.of(new Room("V", 1, false)));
        when(jdbcService.updateRoomStatus(ArgumentMatchers.eq("V"), anyLong(), anyLong())).thenReturn(true);
        Assertions.assertEquals("Room V is booked", bookingService.bookRoom("V", 20));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if values are valid but room is occupied")
    void bookRoom_roomOccupied_messageRoomOccupied() {
        when(jdbcService.getRoom("V")).thenReturn(Optional.of(new Room("V", 1, true)));
        Assertions.assertEquals("Room V is occupied", bookingService.bookRoom("V", 20));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if booking time is more than 2 hours")
    void bookRoom_timeIsMoreThanAllowed_bookingIsNotAllowed() {
        Assertions.assertEquals("Maximum allowed time for booking is 2 hours", bookingService.bookRoom("V", 121));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if booking time is less than 15 minutes")
    void bookRoom_timeLessThanAllowed_bookingIsNotAllowed() {
        Assertions.assertEquals("Minimum allowed time for booking is 15 minutes", bookingService.bookRoom("V", 14));
    }
}
