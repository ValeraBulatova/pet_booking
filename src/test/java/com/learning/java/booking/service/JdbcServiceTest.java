package com.learning.java.booking.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JdbcServiceTest {

    @Test
    void bookRoomInDataBase() {
        long startBookingSeconds = 37828;
        long endBookingSeconds = 879883;
        String roomName = "B";
        String request = String.format("update rooms set is_free = 'false', " +
                "book_time = %d," +
                "book_for = %d" +
                "where name = '%s'", startBookingSeconds, endBookingSeconds, roomName);


        System.out.println(request);

    }
}