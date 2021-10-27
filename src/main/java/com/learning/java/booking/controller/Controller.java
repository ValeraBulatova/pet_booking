package com.learning.java.booking.controller;

import com.learning.java.booking.model.BookRequest;
import com.learning.java.booking.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    private final BookingService services;

    public Controller(BookingService services) {
        this.services = services;
    }

    @SuppressWarnings("unused")
    @GetMapping("/valera")
    public String getRoomStatus(@RequestParam(value = "name", defaultValue = "B") String name) {
        return services.getRoomStatus(name);
    }

    @SuppressWarnings("unused")
    @PostMapping("/valera")
    public String bookRequiredRoom(@RequestBody BookRequest req) {
        return services.bookRoom(req.getRoomName(), req.getMinutes());
    }
}
