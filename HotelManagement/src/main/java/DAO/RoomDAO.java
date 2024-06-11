package DAO;

import model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final Connection connection;

    public RoomDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Room> getAvailableRoomsByHotelId(int hotelId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.room_number, r.room_category, r.is_occupied, r.hotel_id, " +
                "CASE WHEN r.is_occupied THEN res.reservation_number ELSE NULL END AS reservation_number " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.room_number = res.room_number AND res.is_cancelled = FALSE " +
                "WHERE r.hotel_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String roomNumber = resultSet.getString("room_number");
                    String roomCategory = resultSet.getString("room_category");
                    boolean isOccupied = resultSet.getBoolean("is_occupied");
                    String reservationNumber = resultSet.getString("reservation_number");
                    rooms.add(new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber));
                }
            }
        }
        return rooms;
    }

    public void updateRoomStatus(String roomNumber, boolean isOccupied) throws SQLException {
        String sql = "UPDATE rooms SET is_occupied = ?, reservation_number = NULL WHERE room_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isOccupied);
            statement.setString(2, roomNumber);
            statement.executeUpdate();
        }
    }

    public Room getRoomByRoomNumber(String roomNumber) throws SQLException {
        String sql = "SELECT r.id, r.room_number, r.room_category, r.is_occupied, r.hotel_id, " +
                "CASE WHEN r.is_occupied THEN res.reservation_number ELSE NULL END AS reservation_number " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.room_number = res.room_number AND res.is_cancelled = FALSE " +
                "WHERE r.room_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String roomCategory = resultSet.getString("room_category");
                    boolean isOccupied = resultSet.getBoolean("is_occupied");
                    int hotelId = resultSet.getInt("hotel_id");
                    String reservationNumber = resultSet.getString("reservation_number");
                    return new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber);
                }
            }
        }
        return null;
    }
}
