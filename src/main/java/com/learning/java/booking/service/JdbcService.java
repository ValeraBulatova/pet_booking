package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import com.learning.java.booking.model.RoomMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JdbcService implements DAO {

    private final Logger LOGGER = LoggerFactory.getLogger(JdbcService.class);

    private final JdbcTemplate jdbcTemplate;

    public JdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Room> getRoom(String name) {

        return jdbcTemplate.query(String.format("select * from rooms where name = '%s' limit 100", name), new RoomMapper()).stream().findAny();
//        return jdbcTemplate.query("select * from rooms where name = ? limit 100", name, new BeanPropertyRowMapper<>(Room.class)).stream().findAny();

    }

    /**
     *
     * @param roomName name of the room
     * @param startBook epoch seconds; if value > 0 - book room, else - set room free
     * @param endBook epoch seconds
     * @return true if update was successful
     */
    @Override
    public boolean updateRoomStatus(String roomName, long startBook, long endBook) {

        Optional<Room> optionalRoom = getRoom(roomName);
        if (!optionalRoom.isPresent()) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            return false;
        }
        Room room = optionalRoom.get();

        boolean statusToUpdate = !room.isOccupied();
        String query = getUpdateQuery(roomName, statusToUpdate, startBook, endBook);
        LOGGER.debug("Query is: " + query);

        return updateWithRetry(query, roomName, statusToUpdate,  0);
    }

    private boolean updateWithRetry(String query, String roomName, boolean statusToUpdate, int counter) {

        int result = jdbcTemplate.update(query);
        LOGGER.debug("Request to DB was send; result = " + result);

        if (result > 0) {
            LOGGER.info(String.format("Request to update status to %b, for room %s was executed", statusToUpdate, roomName));
        } else if (result == 0 && counter < 2) {
            counter++;
            updateWithRetry(query, roomName, statusToUpdate, counter);
            LOGGER.warn(String.format("Request was executed + %d times", counter));
        } else {
            LOGGER.error(String.format("Request to update status to %s, for room %s failed twice", statusToUpdate, roomName));
        }

        return result != 0;
    }

    private String getUpdateQuery(String roomName, boolean occupied, long bookStart, long bookEnd) {

        String startTime = null;
        String endTime = null;
        if (bookStart > 0) {
            startTime = String.valueOf(bookStart);
            endTime = String.valueOf(bookEnd);
        }
        return String.format("update rooms set occupied = '%b', " +
                "book_start = %s, " +
                "book_end = %s " +
                "where name = '%s'", occupied, startTime, endTime, roomName);
    }

}
