package com.learning.java.booking;
import com.learning.java.booking.rooms.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration // read docs
public class Config {

    @SuppressWarnings("unused")
    @Bean
    public Map<String, Room> listRoom() {
        Map <String, Room> listRoom = new HashMap<>();
        listRoom.put("A", new Room("A", 1, true));
        listRoom.put("B", new Room("B", 2, false));
        listRoom.put("C", new Room("C", 3, true));
       return  listRoom;
    }

}
