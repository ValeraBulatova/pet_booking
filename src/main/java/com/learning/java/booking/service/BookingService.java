package com.learning.java.booking.service;


import com.learning.java.booking.model.Room;
import com.learning.java.booking.model.RoomResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BookingService {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final JpaService jpaService;

    public BookingService(JpaService jpaService) {
        this.jpaService = jpaService;
    }

    /**
     * Provide information is room occupied
     * @param name - required room
     * @return information about room
     */
    public ResponseEntity<RoomResponse> getRoomStatus(String name) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (StringUtils.isEmpty(name)) {
            responseHeaders.set("Status code", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(new RoomResponse("Please, input the room name", HttpStatus.BAD_REQUEST), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Room room = jpaService.getRoom(name);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", name));
            responseHeaders.set("Status code", String.valueOf(HttpStatus.NOT_FOUND));
            return new ResponseEntity<>(new RoomResponse(String.format("Room %s was not found", name), HttpStatus.NOT_FOUND), responseHeaders, HttpStatus.NOT_FOUND);
        }

        responseHeaders.set("Status code", String.valueOf(HttpStatus.OK));
        String status = room.isOccupied() ? "occupied" : "free";
        return new ResponseEntity<>(new RoomResponse(String.format("Room %s is %s", room.getName(), status), HttpStatus.OK), responseHeaders, HttpStatus.OK);
    }

    public ResponseEntity<RoomResponse> bookRoom(String roomName, int minutes) {

        HttpHeaders responseHeaders = new HttpHeaders();
        if (minutes > 120) {
            responseHeaders.set("Status code", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(new RoomResponse("Maximum allowed time for booking is 2 hours", HttpStatus.BAD_REQUEST), responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (minutes < 1) {
            responseHeaders.set("Status code", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(new RoomResponse("Minimum allowed time for booking is 15 minutes", HttpStatus.BAD_REQUEST), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(roomName)) {
            responseHeaders.set("Status code", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(new RoomResponse("Please input the room name", HttpStatus.BAD_REQUEST), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Room room = jpaService.getRoom(roomName);
        if (room == null) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            responseHeaders.set("Status code", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(new RoomResponse("Invalid room name", HttpStatus.BAD_REQUEST), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (room.isOccupied()) {
            responseHeaders.set("Status code", String.valueOf(HttpStatus.OK));
            return new ResponseEntity<>(new RoomResponse(String.format("Room %s is occupied", roomName), HttpStatus.OK), responseHeaders, HttpStatus.OK);
        }

        executorService.schedule(() -> jpaService.updateRoomStatus(roomName, 0, 0 ), minutes, TimeUnit.MINUTES);

        Instant now = Instant.now();

        long timeCalculation = now.getEpochSecond();

        Integer bookStart = Math.toIntExact(timeCalculation);
        Integer bookEnd = Math.toIntExact(timeCalculation + TimeUnit.MINUTES.toSeconds(minutes));
        jpaService.updateRoomStatus(roomName, bookStart, bookEnd);

        String message = jpaService.getRoom(roomName).isOccupied() ? String.format("Room %s is booked", roomName)
                : String.format("Room %s is NOT booked due to internal error", roomName);
        responseHeaders.set("Status code", String.valueOf(HttpStatus.CREATED));
        return new ResponseEntity<>(new RoomResponse(message, HttpStatus.CREATED), responseHeaders, HttpStatus.CREATED);
    }
}
