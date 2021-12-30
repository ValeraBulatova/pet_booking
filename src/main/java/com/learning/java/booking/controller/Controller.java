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
    public ResponseEntity<RoomStatus> getRoomStatus(@PathVariable(value="name") String name) {
        try {
            String roomStatus = services.getRoomStatus(name);
            return new ResponseEntity<>(new RoomStatus(roomStatus, 200), HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(), 404), HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("unused")
    @PostMapping("/room/book")
    public ResponseEntity<RoomStatus> bookRequiredRoom(@RequestBody BookRequest req) {
        try {
            String roomName = req.getRoomName();
            boolean isBooked = services.bookRoom(roomName, req.getMinutes());
            String message = isBooked ? String.format("Room %s is booked", roomName)
                    : String.format("Room %s is occupied", roomName);
            return new ResponseEntity<>(new RoomStatus(message, 200), HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(),400), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new RoomStatus(e.getMessage(),404), HttpStatus.NOT_FOUND);
        }
    }
}
