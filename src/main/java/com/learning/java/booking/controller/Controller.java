package com.learning.java.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        return services.getRoomStatus(name);
    }
}
