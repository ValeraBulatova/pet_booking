package com.learning.java.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServicesTest {


    static Services services;

    public ServicesTest(Services services) {
        this.services = services;
    }

    @Test
    void testGetRoomStatus() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> services.getRoomStatus(null));
//        Assertions.assertTrue(1 ==1);
    }
}
