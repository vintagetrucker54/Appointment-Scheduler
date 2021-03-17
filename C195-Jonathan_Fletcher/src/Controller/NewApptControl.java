package Controller;

/** @author Jonathan Fletcher */

import DBUtilities.DBQuery;
import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the New Appointment Screen.
 */
public class NewApptControl implements Initializable {

    @FXML private ComboBox<User> user_CBOX;
    @FXML private ComboBox<Customer> cust_CBOX;
    @FXML private ComboBox<LocalTime> start_CBOX;
    @FXML private ComboBox<LocalTime> end_CBOX;
    @FXML private ComboBox<Contacts> contact_CBOX;
    @FXML private ComboBox<String> type_CBOX;

    @FXML private TextField title_txtFld;
    @FXML private TextField desc_txtFld;
    @FXML private TextField loc_txtFld;

    @FXML private Label AddAppt_msg;

    @FXML private DatePicker DatePicker;

    DBQuery dbQuery = new DBQuery();

    /**
     * This method loads main Appointment Screen. Used to compress code.
     * @param event button click
     * @throws IOException
     */
    public void returnToMain(ActionEvent event) throws IOException {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/Windows/Appointment.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
    }


    /**
     * This method returns to Main Appointment Screen after verification.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void Cancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel?");
        Optional<ButtonType> result = alert.showAndWait();
        returnToMain(event);
    }





    /** This method is used to save user-entered information and add an appt to the scheduler.
     @param event button click
     */
    @FXML
    void Save(ActionEvent event) {
        //Ensure no empty fields & alert if empty
        LocalDate date = DatePicker.getValue();
        if (user_CBOX.getValue() == null || cust_CBOX.getValue() == null || title_txtFld.getText().isEmpty() || desc_txtFld.getText().isEmpty()
                || loc_txtFld.getText().isEmpty() || contact_CBOX.getValue() == null || type_CBOX.getValue() == null
                || DatePicker.getValue() == null || start_CBOX.getValue() == null || end_CBOX.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Must enter a valid value for each field.");
            alert.showAndWait();
        }
        //check for weekend selections and alert if selected
        else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Appointments must be made within Business Hours:\n Monday - Friday \n 08:00 - 22:00");
            alert.showAndWait();
        }
        //check is past date selected
        else if (date.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText( "Please choose a future date");
            alert.showAndWait();
        }
        //get fields/values
        else {
            try {
                String appointmentTitle = title_txtFld.getText();
                String description = desc_txtFld.getText();
                String location = loc_txtFld.getText();
                String appointmentType = type_CBOX.getValue();
                date = DatePicker.getValue();
                LocalTime startTime = start_CBOX.getValue();
                LocalTime endTime = end_CBOX.getValue();
                LocalDateTime apptStart = LocalDateTime.of(date, startTime);
                LocalDateTime apptEnd = LocalDateTime.of(date, endTime);
                User u = user_CBOX.getValue();
                int userId = u.getUserID();
                Customer c = cust_CBOX.getValue();
                int custId = c.getCustomerId();
                Contacts contacts = contact_CBOX.getValue();
                int contactId = contacts.getContactID();

                //Alert if customer has a conflicting appointment
                Appt conflict = Appt.overlap(custId, apptStart, apptEnd);
                if (conflict != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Existing appointment at the selected time.");
                    alert.setContentText("Appt ID: " + conflict.getApptId() + "\n" +
                            "Date: " + conflict.getApptStart().toLocalDate() + "\n" +
                            "Start: " + conflict.getApptStart().toLocalTime() + "\n" +
                            "End: " + conflict.getApptEnd().toLocalTime()+ "\n" +
                            "Please select a new time...");
                    alert.showAndWait();
                }
                //check if end time is before start time
                else if (apptStart.isBefore(LocalDateTime.now()) || apptEnd.isBefore(LocalDateTime.now()) || apptEnd.isBefore(apptStart)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Appointment start time must be before end time!");
                    alert.showAndWait();
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        returnToMain(event);
                    }else if (result.isPresent() && result.get() == ButtonType.CANCEL){
                        alert.close();
                    }
                }
                else{
                    dbQuery.createAppt(appointmentTitle, description, location, appointmentType,
                            apptStart, apptEnd, custId, userId, contactId);
                    //set in-app message
                    AddAppt_msg.setText("Appointment added!");

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment added!");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        returnToMain(event);
                    }
                }
            } catch (NumberFormatException | IOException | SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Must enter a valid value for each text field.");
                alert.showAndWait();
            }
        }
    }


    /**
     * This method populates the Contacts combo-boxes/cells/list using two lambda expressions.
     * @throws SQLException
     * <p><b> 3D: Lambda Justification -  Lambda expressions used to populate combo-boxes/cells/list and increase readability. </b></p>
     */
    public void setContactComboBox() throws SQLException {
        DBQuery dbQuery = new DBQuery();
        contact_CBOX.setItems(dbQuery.getAllContacts());
        contact_CBOX.getSelectionModel().selectFirst();

        //Factory for the list cells
        Callback<ListView<Contacts>, ListCell <Contacts>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "-" :( "[" + item.getContactID() + "] " + item.getContactName()));
            }
        };
        //Factory for button cell
        Callback <ListView<Contacts>, ListCell<Contacts>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getContactName());
            }
        };
        contact_CBOX.setCellFactory(factory);
        contact_CBOX.setButtonCell(cellFactory.call(null));

    }


    /**
     * Initializes ApptControl class and populates appointments table and combo-boxes.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting","Customer Meeting","Review Meeting","De-Briefing");

        ObservableList<Customer> allCustomersNameID = null;
        ObservableList<User> allUsers = null;

        //fill 'Types' combo-box
        type_CBOX.setItems(appointmentTypes);

        //fill 'Users' combo-box
        try {
            allUsers = dbQuery.getAllUsers();
            allCustomersNameID = dbQuery.getCustID();
            setContactComboBox();
        } catch (SQLException e) {
            System.out.println("Error: "+e);
        }
        user_CBOX.setItems(allUsers);

        //fill 'Customers' combo-box
        cust_CBOX.setItems(allCustomersNameID);
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        //fill 'start/end' combo-box
        LocalTime busHoursStart = LocalTime.of(8, 0);
        LocalTime busHoursEnd = LocalTime.of(22, 0);
        LocalDateTime busHoursStart_conv = LocalDateTime.of(LocalDate.now(), busHoursStart);
        LocalDateTime busHoursEnd_conv = LocalDateTime.of(LocalDate.now(), busHoursEnd);

        ZonedDateTime ESTStart = busHoursStart_conv.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime ESTEnd = busHoursEnd_conv.atZone(ZoneId.of("America/New_York"));

        ZonedDateTime busHoursLStart = ESTStart.withZoneSameInstant(ZoneId.systemDefault());
        ZonedDateTime busHoursLEnd = ESTEnd.withZoneSameInstant(ZoneId.systemDefault());
        LocalTime bhStart = busHoursLStart.toLocalTime();
        LocalTime bhEnd = busHoursLEnd.toLocalTime();

        LocalTime first = bhStart;
        LocalTime last = bhStart.plusMinutes(30);
        while(first.isBefore(bhEnd.minusSeconds(1))){
            start_CBOX.getItems().add(first);
            end_CBOX.getItems().add(last);
            first = first.plusMinutes(30);
            last = first.plusMinutes(30);
        }
    }
}
