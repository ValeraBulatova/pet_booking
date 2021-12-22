package com.learning.java.booking.controller;

import com.learning.java.booking.model.BookRequest;
import com.learning.java.booking.model.RoomResponse;
import com.learning.java.booking.service.BookingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    private final BookingService services;

    public Controller(BookingService services) {
        this.services = services;
    }

    @SuppressWarnings("unused")
    @GetMapping("/rooms/status/{name}")
    public ResponseEntity<RoomResponse> getRoomStatus(@PathVariable(value="name") String name) {
        RoomResponse responseRoom = services.getRoomStatus(name);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Status code", String.valueOf(responseRoom.getCode()));
        return new ResponseEntity<>(responseRoom, responseHeaders, HttpStatus.OK);
    }

    @SuppressWarnings("unused")
    @PostMapping("/rooms/book")
    public ResponseEntity<RoomResponse> bookRequiredRoom(@RequestBody BookRequest req) {
        RoomResponse responseRoom = services.bookRoom(req.getRoomName(), req.getMinutes());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Status code", String.valueOf(responseRoom.getCode()));
        return new ResponseEntity<>(responseRoom, responseHeaders, HttpStatus.CREATED);
    }
}
