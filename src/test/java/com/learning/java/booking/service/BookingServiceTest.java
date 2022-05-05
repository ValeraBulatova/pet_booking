package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceTest {


    private final JpaService jpaService;
    private final BookingService bookingService;


    BookingServiceTest() {
        jpaService = mock(JpaService.class);
        bookingService = new BookingService(jpaService);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    @DisplayName("Room status is not shown, return message with reason, if roomName == null")
    void getRoomStatus_roomNameIsNull_messageInvalidRoomName(String roomName) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
            bookingService.getRoomStatus(roomName));
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains("Please, input the room name"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    @DisplayName("Room is not booked, return message with reason, if roomName == null")
    void bookRoom_roomNameIsNull_messageInvalidRoomName(String roomName) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookRoom(roomName, 20));
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains("Please, input the room name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "Test"})
    @DisplayName("Room is not booked, return message with reason, if there is no room with required roomName")
    void bookRoom_invalidRoomName_messageInvalidRoomName(String roomName) {
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () ->
                bookingService.bookRoom(roomName, 20));
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(String.format("Room %s was not found in database", roomName)));
    }

    @Test
    @DisplayName("Room is booked, if values are valid and room is not occupied")
    void bookRoom_validEntryValues_roomIsBooked() {
        Room room = new Room("V", 1, false);
        when(jpaService.getRoom("V")).thenReturn(room);
        when(jpaService.updateRoomStatus(ArgumentMatchers.eq(room), anyLong(), anyLong())).thenReturn(true);
        Assertions.assertEquals("Room V is booked", bookingService.bookRoom("V", 20));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if values are valid but room is occupied")
    void bookRoom_roomOccupied_messageRoomOccupied() {
        when(jpaService.getRoom("V")).thenReturn(new Room("V", 1, true));
        Assertions.assertEquals("Room V is occupied", bookingService.bookRoom("V", 20));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if booking time is more than 2 hours")
    void bookRoom_timeIsMoreThanAllowed_bookingIsNotAllowed() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookRoom("V", 121));
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains("Maximum allowed time for booking is 2 hours"));
    }

    @Test
    @DisplayName("Room is not booked, return message with reason, if booking time is less than 15 minutes")
    void bookRoom_timeLessThanAllowed_bookingIsNotAllowed() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookRoom("V", 14));
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains("Minimum allowed time for booking is 15 minutes"));
    }
}
