package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.ClientService;
import service.ClientRatingService;
import service.AdditionalServiceService;
import service.UserService;
import service.HotelService;
import utils.DatabaseConnection;
import DAO.ClientDAO;
import DAO.ClientRatingDAO;
import DAO.AdditionalServiceDAO;
import DAO.UserDAO;
import DAO.UserRoleDAO;
import DAO.HotelDAO;
import model.Client;
import model.ClientRating;
import model.AdditionalService;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OwnerPanelController {
    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableColumn<Client, Integer> clientIdColumn;
    @FXML
    private TableColumn<Client, String> clientNameColumn;
    @FXML
    private TableColumn<Client, String> clientEmailColumn;

    @FXML
    private TableView<ClientRating> clientRatingTableView;
    @FXML
    private TableColumn<ClientRating, Integer> ratingIdColumn;
    @FXML
    private TableColumn<ClientRating, Integer> ratingClientIdColumn;
    @FXML
    private TableColumn<ClientRating, Integer> ratingColumn;

    @FXML
    private TableView<AdditionalService> additionalServiceTableView;
    @FXML
    private TableColumn<AdditionalService, String> serviceTypeColumn;
    @FXML
    private TableColumn<AdditionalService, String> seasonColumn;
    @FXML
    private TableColumn<AdditionalService, Integer> usageCountColumn;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField hotelNameField;
    @FXML
    private TextField hotelAddressField;

    private ClientService clientService;
    private ClientRatingService clientRatingService;
    private AdditionalServiceService additionalServiceService;
    private UserService userService;
    private HotelService hotelService;
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<ClientRating> clientRatingData = FXCollections.observableArrayList();
    private ObservableList<AdditionalService> additionalServiceData = FXCollections.observableArrayList();
    private User currentUser; // Owner'ın bilgileri

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadClientData();
        loadClientRatingData();
        loadAdditionalServiceData();
    }

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            clientService = new ClientService(new ClientDAO(connection));
            clientRatingService = new ClientRatingService(new ClientRatingDAO(connection));
            additionalServiceService = new AdditionalServiceService(new AdditionalServiceDAO(connection));
            userService = new UserService(new UserDAO(connection), new UserRoleDAO(connection), new HotelDAO(connection));
            hotelService = new HotelService(new HotelDAO(connection));

            // Client TableView sütunlarını başlatma
            clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            clientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

            // ClientRating TableView sütunlarını başlatma
            ratingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            ratingClientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
            ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

            // AdditionalService TableView sütunlarını başlatma
            serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
            seasonColumn.setCellValueFactory(new PropertyValueFactory<>("season"));
            usageCountColumn.setCellValueFactory(new PropertyValueFactory<>("usageCount"));

            // TableView verilerini başlatma
            clientTableView.setItems(clientData);
            clientRatingTableView.setItems(clientRatingData);
            additionalServiceTableView.setItems(additionalServiceData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClientData() {
        if (currentUser == null) {
            return; // currentUser null ise işlem yapma
        }

        try {
            List<Client> clients = clientService.getClientsByHotelId(currentUser.getHotelId());
            clientData.setAll(clients);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClientRatingData() {
        if (currentUser == null) {
            return; // currentUser null ise işlem yapma
        }

        try {
            List<ClientRating> ratings = clientRatingService.getClientRatingsByHotelId(currentUser.getHotelId());
            clientRatingData.setAll(ratings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAdditionalServiceData() {
        if (currentUser == null) {
            return; // currentUser null ise işlem yapma
        }

        try {
            List<AdditionalService> services = additionalServiceService.getAdditionalServicesByHotelId(currentUser.getHotelId());
            additionalServiceData.setAll(services);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateManager() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "All fields are required!");
            return;
        }

        try {
            int hotelId = currentUser.getHotelId();
            userService.createUserWithRole(username, password, "manager", hotelId);
            showAlert("Success", "Manager created successfully!");
            clearUsernameField();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the manager. Please try again.");
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
    private void clearUsernameField(){
        usernameField.clear();
        passwordField.clear();
    }
}
