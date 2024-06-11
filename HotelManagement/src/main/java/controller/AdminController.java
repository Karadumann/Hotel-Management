package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import service.HotelService;
import service.UserService;
import utils.DatabaseConnection;
import DAO.UserRoleDAO;
import DAO.UserDAO;
import DAO.HotelDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField hotelNameField;
    @FXML
    private TextField hotelAddressField;

    private UserService userService;
    private HotelService hotelService;

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            userService = new UserService(new UserDAO(connection), new UserRoleDAO(connection), new HotelDAO(connection));
            hotelService = new HotelService(new HotelDAO(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateOwner() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String hotelName = hotelNameField.getText().trim();
        String hotelAddress = hotelAddressField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || hotelName.isEmpty() || hotelAddress.isEmpty()) {
            showAlert("Input Error", "All fields are required!");
            return;
        }

        try {
            int hotelId = hotelService.createHotel(hotelName, hotelAddress);
            userService.createUserWithRole(username, password, "owner", hotelId);
            showAlert("Success", "Hotel and owner created successfully!");
            clearUserNameField();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the hotel and owner. Please try again.");
        }
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while going back. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearUserNameField(){
        usernameField.clear();
        passwordField.clear();
        hotelNameField.clear();
        hotelAddressField.clear();

    }
}
