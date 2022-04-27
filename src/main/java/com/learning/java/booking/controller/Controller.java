package com.learning.java.booking.controller;

import com.learning.java.booking.model.BookRequest;
import com.learning.java.booking.model.RoomStatus;
import com.learning.java.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;


@RestController
public class Controller {

    private final BookingService services;

    public Controller(BookingService services) {
        this.services = services;
    }

    @SuppressWarnings("unused")
    @GetMapping("/room/{name}/status")
    @ResponseStatus
    public ResponseEntity getRoomStatus(@PathVariable(value="name") String name) {
        try {
            return new ResponseEntity<>(new RoomStatus(services.getRoomStatus(name), 200), HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(), 404), HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("unused")
    @PostMapping("/room/book")
    public ResponseEntity bookRequiredRoom(@RequestBody BookRequest req) {
        try {
            return new ResponseEntity<>(new RoomStatus(services.bookRoom(req.getRoomName(), req.getMinutes()), 202), HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(),400), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(),404), HttpStatus.NOT_FOUND);
        }
    }
}
