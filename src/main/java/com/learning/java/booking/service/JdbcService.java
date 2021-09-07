package com.learning.java.booking.service;

import com.learning.java.booking.model.Room;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;


@Service
public class JdbcService {

    private static final System.Logger LOGGER = System.getLogger("jdbcLogger");

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
            // TODO: 06.09.2021 handel exception
            e.printStackTrace();
            return 0;
        }

    }
// TODO: 06.09.2021 make in controller

    Map<String, Room> geAllRooms() {

        try(Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from rooms");

            Map<String, Room> rooms = new HashMap<>();
            while (resultSet.next()) {
                Room room = mapResultToRoom(resultSet);
                rooms.put(room.getName(), room);
            }
            LOGGER.log(System.Logger.Level.INFO, String.format("%d rooms were fetched", rooms.size()));
            return rooms;

        } catch (SQLException e) {
            // TODO: 20.08.2021 handle exception
            e.printStackTrace();
            return null;
        }
    }

    private Room mapResultToRoom(ResultSet result) throws SQLException {
        // TODO: 20.08.2021 check for empty
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


    boolean unbookRoomInDataBase(String roomName){
        String request = String.format("update rooms set is_free = 'true', " +
                "book_time = null, " +
                "book_for = null " +
                "where name = '%s'", roomName);
        int result = requestToDataBase(request);
        return result != 0;
    }

}
