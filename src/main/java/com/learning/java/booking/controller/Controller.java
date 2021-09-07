package com.learning.java.booking.controller;

import com.learning.java.booking.model.BookRequest;
import org.springframework.web.bind.annotation.*;
import com.learning.java.booking.service.Services;


@RestController
public class Controller {

    private Services services;

    public Controller(Services services) {
        this.services = services;
    }

    //	open http://localhost:8080/valera to see the result

    @GetMapping("/valera")
    public String getRoomStatus(@RequestParam(value = "name", defaultValue = "B") String name) {
//        return services.getRoomStatus(name);
        return services.getStatusOfAllRooms();
    }

    @PostMapping("/valera")
    public String bookRequiredRoom(@RequestBody BookRequest req) {
        return services.bookRoom(req.roomName, req.minutes);
    }
}
