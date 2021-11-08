package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

// TODO: 27.10.2021 injec attack
@Service
public class JdbcService implements DAO {

    private final Logger LOGGER = LoggerFactory.getLogger(JdbcService.class);

    private static final String JDBC_DRIVER     = "org.h2.Driver";
    private static final String DB_URL          = "jdbc:h2:~/test";

    private static final String USER            = "sa";
    private static final String PASS            = "";

    private int requestCounting = 0;

    public JdbcService() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
    }

    private int requestToDataBase(String query){
        try(Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement()) {

            return statement.executeUpdate(query);
        } catch (SQLException e){
            LOGGER.error("SQL request failed", e);
            return 0;
        }
    }


    private Room mapResultToRoom(ResultSet result) throws SQLException {
        String name = result.getString("name");
        int id = result.getInt("id");
        boolean free = result.getBoolean("is_free");
        return new Room(name, id, free);
    }


    boolean bookRoomInDataBase(String roomName, long startBookingSeconds, long endBookingSeconds) {
        String request = String.format("update rooms set is_free = 'false', " +
                "book_time = %d, " +
                "book_for = %d " +
                "where name = '%s'", startBookingSeconds, endBookingSeconds, roomName);
        int result = requestToDataBase(request);
        return result != 0;
    }


    boolean unbookRoomInDataBase(String roomName) {
        String request = String.format("update rooms set is_free = 'true', " +
                "book_time = null, " +
                "book_for = null " +
                "where name = '%s'", roomName);
        int result = requestToDataBase(request);
        if (result != 0) {
            LOGGER.info(String.format("Request to set room %s free was executed", roomName));
        } else {
            LOGGER.error(String.format("Request to set room %s free failed", roomName));
        }
        return result != 0;
    }

    @Override
    public Room getRoom(String name) {

        Room room = null;

        try (Connection connection = DriverManager.getConnection(DB_URL,USER,PASS)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from rooms where name = ?");
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            room = mapResultToRoom(resultSet);

            return room;

        } catch (SQLException e) {
            LOGGER.error("SQL Request failed", e);
            return room;
        }
    }

    @Override
    public Map<String, Room> getAllRooms() {
        Map<String, Room> rooms = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from rooms limit 100");

            while (resultSet.next()) {
                Room room = mapResultToRoom(resultSet);
                rooms.put(room.getName(), room);
            }
            LOGGER.info(String.format("%d rooms were fetched", rooms.size()));
            return rooms;

        } catch (SQLException e) {
            LOGGER.error("SQL Request failed", e);
            return rooms;
        }
    }

    @Override
    public boolean updateRoomStatus(String roomName, long startBookingSeconds, long endBookingSeconds) {

        String isRoomFree = "true";
        String bookTime = null;
        String bookFor = null;

        Room room = getAllRooms().get(roomName);

        if (room.isFree()) {
            isRoomFree = "false";
            bookTime = String.valueOf(startBookingSeconds);
            bookFor = String.valueOf(endBookingSeconds);
        }

        String query = String.format("update rooms set is_free = '%s', " +
                "book_time = %s, " +
                "book_for = %s " +
                "where name = '%s'", isRoomFree, bookTime, bookFor, roomName);

        LOGGER.debug("Query is: " + query);

        int result = requestToDataBase(query);
        LOGGER.debug("Request to DB was send; result = " + result);

        if (result != 0) {
            LOGGER.info(String.format("Request to update status to %s, for room %s was executed", isRoomFree, roomName));
        } else if (requestCounting == 0 && result == 0) {
            LOGGER.warn(String.format("Request was executed + %d times", requestCounting));
            requestCounting++;
            updateRoomStatus(roomName, startBookingSeconds, endBookingSeconds);
            LOGGER.warn("Request was executed twice");
        } else {
            LOGGER.error(String.format("Request to update status to %s, for room %s failed twice", isRoomFree, roomName));
        }

        return result!= 0;
    }
}
