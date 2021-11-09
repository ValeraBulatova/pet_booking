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
        boolean free = result.getBoolean("is_free");
        return new Room(name, id, free);
    }

    @Override
    public Optional<Room> getRoom(String name) {

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {

            // TODO: 09.11.2021 place limit
            PreparedStatement preparedStatement = connection.prepareStatement("select * from rooms where name = ?");
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            Room room = mapResultToRoom(resultSet);

            return Optional.of(room);

        } catch (SQLException e) {
            LOGGER.error("SQL request failed", e);
            return Optional.empty();
        }
    }

    // TODO: 08.11.2021 obsolete, remove
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

    // TODO: 08.11.2021 add docs, when passed with 0 - unbook
    /**
     *
     * @param roomName
     * @param startBook epoch seconds
     * @param endBook epoch seconds
     * @return
     */
    @Override
    public boolean updateRoomStatus(String roomName, long startBook, long endBook) {

        // TODO: 08.11.2021 replace with querying exact room
        Room room = getAllRooms().get(roomName);

        boolean updateStatus = !room.isFree();
        String query = getUpdateQuery(roomName, updateStatus, startBook, endBook);
        LOGGER.debug("Query is: " + query);

        return updateWithRetry(query, roomName, 0);
    }

    private boolean updateWithRetry(String query, String roomName, int counter) {
        // TODO: 09.11.2021 handle
        String updateStatus = "TODO";

        int result = requestToDataBase(query);
        LOGGER.debug("Request to DB was send; result = " + result);

        if (result > 0) {
            LOGGER.info(String.format("Request to update status to %s, for room %s was executed", updateStatus, roomName));
        } else if (result == 0 && counter < 2) {
            counter++;
            updateWithRetry(query, roomName, counter);
            LOGGER.warn(String.format("Request was executed + %d times", counter));
        } else {
            LOGGER.error(String.format("Request to update status to %s, for room %s failed twice", updateStatus, roomName));
        }

        return result != 0;
    }

    // TODO: 09.11.2021 replace book_time -> book_start, book_for -> book_end
    // TODO: 09.11.2021 finish
    private String getUpdateQuery(String roomName, boolean isFree, long bookStart, long bookEnd) {
        return String.format("update rooms set is_free = 'true', " +
                "book_time = %d, " +
                "book_for = null " +
                "where name = '%s'", roomName);
    }

}
