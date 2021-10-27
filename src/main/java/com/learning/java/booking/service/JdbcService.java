package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
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


    // TODO: 27.10.2021 add retry
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
        // TODO: 27.10.2021 learn SQL injection attacks
        // https://www.youtube.com/watch?v=ciNHn38EyRc&ab_channel=Computerphile

        // TODO: 27.10.2021 implement
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Room> getAllRooms() {
        Map<String, Room> rooms = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from rooms");

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
        // TODO: 27.10.2021 implement from two previous
        throw new NotImplementedException();    }
}
