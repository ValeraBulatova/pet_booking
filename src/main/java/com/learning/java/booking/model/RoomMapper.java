package com.learning.java.booking.model;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        int id = rs.getInt("id");
        boolean occupied = rs.getBoolean("occupied");
        return new Room(name, id, occupied);
    }
}
