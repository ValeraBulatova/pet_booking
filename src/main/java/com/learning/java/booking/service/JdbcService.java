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
import java.util.Optional;

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
        boolean free = result.getBoolean("occupied");
        return new Room(name, id, free);
    }

    @Override
    public Optional<Room> getRoom(String name) {

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from rooms where name = ? limit 100");
            preparedStatement.setString(1, name);

            LOGGER.debug("QUERY: " + preparedStatement.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            Room room = mapResultToRoom(resultSet);

            return Optional.of(room);

        } catch (SQLException e) {
            LOGGER.error("SQL request failed", e);
            return Optional.empty();
        }
    }

    /**
     *
     * @param roomName
     * @param startBook epoch seconds; if value > 0 - book room, else - set room free
     * @param endBook epoch seconds
     * @return true if update was successful
     */
    @Override
    public boolean updateRoomStatus(String roomName, long startBook, long endBook) {

        if (!getRoom(roomName).isPresent()) {
            LOGGER.info(String.format("Room %s was not found in database", roomName));
            return false;
        }
        Room room = getRoom(roomName).get();

        boolean updateStatus = !room.isOccupied();
        String query = getUpdateQuery(roomName, updateStatus, startBook, endBook);
        LOGGER.debug("Query is: " + query);

        return updateWithRetry(query, roomName, 0);
    }

    private boolean updateWithRetry(String query, String roomName, int counter) {

        Room room = getRoom(roomName).get();
        boolean updateStatus = !room.isOccupied();

        int result = requestToDataBase(query);
        LOGGER.debug("Request to DB was send; result = " + result);

        if (result > 0) {
            LOGGER.info(String.format("Request to update status to %b, for room %s was executed", updateStatus, roomName));
        } else if (result == 0 && counter < 2) {
            counter++;
            updateWithRetry(query, roomName, counter);
            LOGGER.warn(String.format("Request was executed + %d times", counter));
        } else {
            LOGGER.error(String.format("Request to update status to %s, for room %s failed twice", updateStatus, roomName));
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
                "book_for = %s, " +
                "where name = '%s'", occupied, startTime, endTime, roomName);
    }

}
