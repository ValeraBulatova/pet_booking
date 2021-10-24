package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;


@Service
public class JdbcService {

    private final Logger LOGGER = LoggerFactory.getLogger(JdbcService.class);

    private static final String JDBC_DRIVER     = "org.h2.Driver";
    private static final String DB_URL          = "jdbc:h2:~/test";

    private static final String USER            = "sa";
    private static final String PASS            = "";

    public JdbcService() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
    }

    private int requestToDataBase(String query){
        try(Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement statement = connection.createStatement()){

            return statement.executeUpdate(query);

        }catch (SQLException e){
            LOGGER.error("SQL request failed", e);
            return 0;
        }

    }

    Map<String, Room> geAllRooms() {

        try(Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from rooms");

            Map<String, Room> rooms = new HashMap<>();
            while (resultSet.next()) {
                Room room = mapResultToRoom(resultSet);
                assert room != null;
                rooms.put(room.getName(), room);
            }
            LOGGER.info(String.format("%d rooms were fetched", rooms.size()));
            return rooms;

        } catch (SQLException e) {
            LOGGER.error("SQL Request failed", e);
            return null;
        }
    }

    private Room mapResultToRoom(ResultSet result) throws SQLException {
        if(result != null) {
            String name = result.getString("name");
            int id = result.getInt("id");
            boolean free = result.getBoolean("is_free");
            return new Room(name, id, free);
        }else {
            LOGGER.error("Room was not received from database");
            return null;
        }
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
        LOGGER.info(String.format("Request to set room %s free was executed", roomName));
        return result != 0;
    }

}
