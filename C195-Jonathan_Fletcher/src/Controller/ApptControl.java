package Controller;

/** @author Jonathan Fletcher */

import Models.*;
import DBUtilities.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * Controller for the Appointments Screen.
 */
public class ApptControl implements Initializable {

    @FXML private Button addAppt_BTN;
    @FXML private Button modAppt_BTN;
    @FXML private Button addCust_BTN;
    @FXML private Button modCust_BTN;
    @FXML private Button reports_BTN;

    @FXML private TableView<Appt> apptTableView;
    @FXML private TableColumn<Appt, Integer> apptID_col;
    @FXML private TableColumn<Appt, Integer> custID_col;
    @FXML private TableColumn<Appt, String> title_col;
    @FXML private TableColumn<Appt, String> desc_col;
    @FXML private TableColumn<Appt, String> contact_col;
    @FXML private TableColumn<Appt, String> loc_col;
    @FXML private TableColumn<Appt, String> type_col;
    @FXML private TableColumn<Appt, LocalDateTime> start_col;
    @FXML private TableColumn<Appt, LocalDateTime> end_col;

    @FXML private DatePicker DatePicker;


    Stage stage;
    Parent scene;
    DBQuery dbQuery = new DBQuery();
    private final ObservableList<Customer> allCust = FXCollections.observableArrayList();
    private final ObservableList<Appt> allAppts = dbQuery.getAllAppts();
    private final ObservableList<Appt> monthlyAppts = FXCollections.observableArrayList();
    private final ObservableList<Appt> weeklyAppts = FXCollections.observableArrayList();
    ObservableList<String> apptTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting","Customer Meeting","Review Meeting","De-Briefing");
    private static String currentUser;

    /**
     * This method loads a new Appointment Screen.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void ADD_Appt(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Windows/NewAppt.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method loads the Modify Appointment Screen.
     * @param event button click
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    void MOD_Appt(ActionEvent event) throws IOException, SQLException {
            Stage stage;
            Parent root;
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Windows/ModAppt.fxml"));
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }


    /**
     * This method loads New Customer Screen.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void NAV_to_newCust(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Windows/NewCust.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method  loads the Modify Customer Screen.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void NAV_to_modCust(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Windows/ModCust.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method  loads Reports Screen.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void NAV_to_Reports(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Windows/Reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method is to view appointments for the current week only. It will not show future or past appointments.
     * It always starts at Sunday of the week.
     * @param event radiobutton click
     */
    @FXML
    void showWeeklyAppts(ActionEvent event){
        getTableData();
        LocalDate date = DatePicker.getValue();
        apptTableView.setItems(dbQuery.getWeeklyAppts(date));
    }

    /**
     * This method is to view appointments for the current month only. It will not show future or past appointments.
     * It always starts on the first of the month.
     * @param event radiobutton click
     */
    @FXML
    void showMonthlyAppts(ActionEvent event) {
        getTableData();
        int monthInt = 0;
        apptTableView.setItems(DBQuery.getMonthlyAppts(monthInt));
    }

    /**
     * This method shows all appointments.
     * @param event radiobutton click
     */
    @FXML
    void showAllAppts(ActionEvent event) {
        getTableData();
        apptTableView.setItems(allAppts);
    }

    /**
     * This method checks runs a 15min check and throws alert.
     * @param username currently logged user
     * @throws SQLException
     */
    public void upcomingAppts(String username) throws SQLException {
        currentUser = username;
        ObservableList<Appt> allAppts = dbQuery.getAllAppts();
        ObservableList<User> allUsers = dbQuery.getAllUsers();
        Appt upcomingAppts = null;
        User user = null;

        //get username entered at login
        for(User u : allUsers){
            if(u.getUserName().equals(username)){
                user = u;
            }
        }

        //get possible appt within 15 min
        for(Appt a : allAppts){
            long timeDifference = ChronoUnit.MINUTES.between(LocalDateTime.now(), a.getApptStart());
            assert user != null;
            if(a.getUserID() == user.getUserID() && timeDifference > 0 && timeDifference <= 15) {
                upcomingAppts = a;
            }
        }

        //display message if appointment within 15 min
        if(upcomingAppts != null){
            LocalDate appointmentDate = upcomingAppts.getApptStart().toLocalDate();
            LocalTime appointmentTime = upcomingAppts.getApptStart().toLocalTime();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Upcoming appointment within 15 minutes.");
            alert.setContentText("Appt ID: " + upcomingAppts.getApptId() + "\n" +
                    " Date: " + appointmentDate + "\n" +
                    " Time: " + appointmentTime);
            alert.showAndWait();
        }

        //if no appt was found, display 'no upcoming appt' reminder
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No upcoming appointments.");
            alert.showAndWait();
        }
    }

    /**
     * This method populates table data.
     */
    public void getTableData() {
        apptID_col.setCellValueFactory(new PropertyValueFactory<Appt, Integer>("apptId"));
        custID_col.setCellValueFactory(new PropertyValueFactory<Appt, Integer>("custId"));
        title_col.setCellValueFactory(new PropertyValueFactory<Appt, String>("apptTitle"));
        desc_col.setCellValueFactory(new PropertyValueFactory<Appt, String>("desc"));
        contact_col.setCellValueFactory(new PropertyValueFactory<Appt, String>("contactName"));
        loc_col.setCellValueFactory(new PropertyValueFactory<Appt, String>("loc"));
        type_col.setCellValueFactory(new PropertyValueFactory<Appt, String>("apptType"));
        start_col.setCellValueFactory(new PropertyValueFactory<Appt, LocalDateTime>("apptStart"));
        end_col.setCellValueFactory(new PropertyValueFactory<Appt, LocalDateTime>("apptEnd"));
    }

    /**
     * This method exits program.
     * @param event button click
     */
    @FXML
    void exitProgram(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Initializes ApptControl class and populates appointments table.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getTableData();
        apptTableView.setItems(allAppts);
    }
}
