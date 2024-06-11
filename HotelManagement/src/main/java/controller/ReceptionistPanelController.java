package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import service.ReservationService;
import service.ClientService;
import service.NotificationService;
import utils.DatabaseConnection;
import DAO.ReservationDAO;
import DAO.ClientDAO;
import DAO.RoomDAO;
import DAO.NotificationDAO;
import model.Client;
import model.Reservation;
import model.Room;
import model.User;
import model.Notification;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ReceptionistPanelController {
    @FXML
    private TextField clientNameField;
    @FXML
    private TextField clientEmailField;
    @FXML
    private TextField reservationNumberField;
    @FXML
    private TextField roomNumberField;
    @FXML
    private TextField cancellationTypeField;
    @FXML
    private ComboBox<String> roomCategoryComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField reservationNumberToCancelField;
    @FXML
    private TableView<Room> roomTableView;
    @FXML
    private TableColumn<Room, String> roomNumberColumn;
    @FXML
    private TableColumn<Room, String> roomCategoryColumn;
    @FXML
    private TableColumn<Room, String> roomStatusColumn;
    @FXML
    private TableColumn<Room, String> roomReservationNumberColumn;

    @FXML
    private TableView<Notification> notificationTableView;
    @FXML
    private TableColumn<Notification, String> notificationMessageColumn;
    @FXML
    private TableColumn<Notification, Timestamp> notificationCreatedAtColumn;

    private ClientService clientService;
    private ReservationService reservationService;
    private NotificationService notificationService;
    private ObservableList<Room> roomData;
    private ObservableList<Notification> notificationData = FXCollections.observableArrayList();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadRoomData();
        loadNotifications();
        checkForExpiringReservations();
    }

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            clientService = new ClientService(new ClientDAO(connection));
            reservationService = new ReservationService(new ReservationDAO(connection), new RoomDAO(connection));
            notificationService = new NotificationService(new NotificationDAO(connection), new ReservationDAO(connection));

            // ComboBox için seçenekler ekleniyor
            roomCategoryComboBox.setItems(FXCollections.observableArrayList("Single", "Double", "Suite"));

            // TableView sütunlarını başlatma
            roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
            roomCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("roomCategory"));
            roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            roomReservationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("reservationNumber"));

            notificationMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
            notificationCreatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            notificationTableView.setItems(notificationData);

            // TableView verilerini başlatma
            roomData = FXCollections.observableArrayList();
            roomTableView.setItems(roomData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRoomData() {
        if (currentUser == null) {
            return;
        }

        try {
            List<Room> rooms = reservationService.getAvailableRoomsByHotelId(currentUser.getHotelId());
            roomData.setAll(rooms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkForExpiringReservations() {
        try {
            notificationService.checkAndCreateExpiringReservationNotifications();
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        if (currentUser == null) {
            return;
        }

        try {
            List<Notification> notifications = notificationService.getNotificationsByHotelId(currentUser.getHotelId());
            notificationData.setAll(notifications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateClientAndReservation() {
        try {
            String clientName = clientNameField.getText().trim();
            String clientEmail = clientEmailField.getText().trim();
            String reservationNumber = reservationNumberField.getText().trim();
            String roomNumber = roomNumberField.getText().trim();
            String roomCategory = roomCategoryComboBox.getValue();
            Date startDate = Date.valueOf(startDatePicker.getValue());
            Date endDate = Date.valueOf(endDatePicker.getValue());

            if (clientName.isEmpty() || clientEmail.isEmpty() || reservationNumber.isEmpty() || roomNumber.isEmpty() ||
                    roomCategory == null || startDate == null || endDate == null || startDate.after(endDate)) {
                showAlert("Input Error", "All fields are required and start date must be before end date!");
                return;
            }

            int hotelId = currentUser.getHotelId();

            Client client = new Client(0, clientName, clientEmail, hotelId);
            int clientId = clientService.addClient(client);

            Room selectedRoom = roomData.stream()
                    .filter(r -> r.getRoomNumber().equals(roomNumber) && !r.getIsOccupied() && r.getHotelId() == hotelId)
                    .findFirst()
                    .orElse(null);
            if (selectedRoom == null) {
                showAlert("Error", "Selected room is not available.");
                return;
            }

            Reservation reservation = new Reservation(0, clientId, reservationNumber, roomNumber, "", roomCategory, startDate, endDate, false, hotelId);
            reservationService.addReservation(reservation);

            selectedRoom.setIsOccupied(true);
            selectedRoom.setReservationNumber(reservationNumber);
            reservationService.updateRoomStatus(selectedRoom);
            loadRoomData();

            showAlert("Success", "Client and reservation created successfully!");
            clearCreateClientAndReservation();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancelReservation() {
        String reservationNumber = reservationNumberToCancelField.getText().trim();
        String cancellationType = cancellationTypeField.getText().trim();

        if (reservationNumber.isEmpty() || cancellationType.isEmpty()) {
            showAlert("Input Error", "Reservation Number and Cancellation Type are required!");
            return;
        }

        try {
            reservationService.cancelReservation(reservationNumber, cancellationType);
            showAlert("Success", "Reservation cancelled successfully!");
            loadRoomData();
            clearCreateClientAndReservation();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while cancelling the reservation. Please try again.");
        }
    }


    @FXML
    public void handleRefreshRoomTable() {
        loadRoomData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        ReceptionistMainController.goBackToMain(event, currentUser);
    }

    private void clearCreateClientAndReservation() {
        clientNameField.clear();
        clientEmailField.clear();
        reservationNumberField.clear();
        roomNumberField.clear();
        roomCategoryComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reservationNumberToCancelField.clear();
        cancellationTypeField.clear();
    }
}

