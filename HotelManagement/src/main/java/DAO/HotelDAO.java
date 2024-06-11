package DAO;

import model.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private final Connection connection;

    public HotelDAO(Connection connection) {
        this.connection = connection;
    }

    public int createHotel(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO Hotel (name, address) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, hotel.getName());
            statement.setString(2, hotel.getAddress());
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating hotel failed, no ID obtained.");
            }
        }
    }

}
