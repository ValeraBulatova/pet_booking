package com.learning.java.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.learning.java.booking.service.Services;


@RestController
public class Controller {

//    inject Services in controller
    @Autowired
    private Services services;

//	open http://localhost:8080/valera to see the result

    @GetMapping("/valera")
    public String hello(@RequestParam(value = "name", defaultValue = "A") String name) {
//      use bean to call void
        return services.roomStatus(name);
    }
}
