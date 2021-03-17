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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Modify Appointment Screen.
 */
public class ModApptControl implements Initializable {

    @FXML private TextField apptId_txtFld;
    @FXML private TextField title_txtFld;
    @FXML private TextField desc_txtFld;
    @FXML private TextField loc_txtFld;

    @FXML private ComboBox<Contacts> contactCBOX;
    @FXML private ComboBox<String> apptType_CBOX;
    @FXML private ComboBox<LocalTime> startCBOX;
    @FXML private ComboBox<LocalTime> endCBOX;
    @FXML private ComboBox<User> userCBOX;
    @FXML private ComboBox<Customer> custCBOX;

    @FXML private DatePicker DatePicker;

    @FXML private Label ModApptMessage;

    @FXML private TableView<Appt> MODapptTableView;
    @FXML private TableColumn<Appt, Integer> apptID_col;
    @FXML private TableColumn<Appt, Integer> custID_col;
    @FXML private TableColumn<Appt, String> title_col;
    @FXML private TableColumn<Appt, String> desc_col;
    @FXML private TableColumn<Appt, String> contact_col;
    @FXML private TableColumn<Appt, String> loc_col;
    @FXML private TableColumn<Appt, String> type_col;
    @FXML private TableColumn<Appt, LocalDateTime> start_col;
    @FXML private TableColumn<Appt, LocalDateTime> end_col;

    Appt selectedAppt;
    DBQuery dbQuery = new DBQuery();
    private final ObservableList<Appt> allAppts = dbQuery.getAllAppts();


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
     * This method loads main Appointment Screen when the 'return to...' button clicked.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void Cancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel?");
        alert.showAndWait();
        returnToMain(event);
    }



    /**
     * This method allows appointment delete from DB.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void DEL_appt(ActionEvent event) throws IOException {
        Appt selectedItem = MODapptTableView.getSelectionModel().getSelectedItem();
        int apptId = selectedItem.getApptId();
        String apptType = selectedItem.getApptType();

        //Warning before delete
        Alert alert = new Alert(Alert.AlertType.WARNING, "Delete Appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbQuery.deleteAppt(apptId);

            ModApptMessage.setText("Appointment ID: "+apptId+ "\nAppointment Type: "+apptType+"\nDELETED !!!");

            //popup
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Your appt was deleted.");
            alert.setContentText("Appointment ID: "+apptId+ "\nAppointment Type: "+apptType+"\nDELETED !!!");
            result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                   returnToMain(event);
                }
            }
    }

    /**
     * This method allows appointment update after validations.
     * @param event button click
     */
    @FXML
    void SAVE_update(ActionEvent event) {
        LocalDate date = DatePicker.getValue();

        //Ensure no empty fields
        if (userCBOX.getValue() == null || custCBOX.getValue() == null || title_txtFld.getText().isEmpty()
                || desc_txtFld.getText().isEmpty() || loc_txtFld.getText().isEmpty() || contactCBOX.getValue() == null || apptType_CBOX.getValue() == null
                || DatePicker.getValue() == null || startCBOX.getValue() == null || endCBOX.getValue() == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please enter a valid value for each field.");
            alert.showAndWait();
        }
        //check for weekend selections and alert if selected
        else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Appointments must be made within Business Hours:\n Monday - Friday \n 08:00 - 22:00");
            alert.showAndWait();
        }
        //check is past date selected
        else if (date.isBefore(LocalDate.now()))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText( "Please choose a future date");
            alert.showAndWait();
        }
        //get fields/values
        else {
            try {
                String apptTitle = title_txtFld.getText();
                String desc = desc_txtFld.getText();
                String loc = loc_txtFld.getText();
                String apptType = apptType_CBOX.getValue();
                date = DatePicker.getValue();
                LocalTime startTime = startCBOX.getValue();
                LocalTime endTime = endCBOX.getValue();
                LocalDateTime apptStart = LocalDateTime.of(date, startTime);
                LocalDateTime apptEnd = LocalDateTime.of(date, endTime);
                MODapptTableView.getSelectionModel().getSelectedItem();
                User u = userCBOX.getValue();
                int userId = u.getUserID();
                Customer c = custCBOX.getValue();
                int custId = c.getCustomerId();
                Contacts contacts = contactCBOX.getValue();
                int contactId = contacts.getContactID();

                int apptID = selectedAppt.getApptId();

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

                }else{
                    dbQuery.updateAppt(apptID, apptTitle, desc, loc, apptType,
                            apptStart, apptEnd, custId, userId, contactId);

                    //confirmation of successful update
                    ModApptMessage.setText("Appointment Updated");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment Updated");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        returnToMain(event);
                    }
                }
            } catch (NumberFormatException | IOException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("Must enter a valid value for each field.");
                alert.showAndWait();
            }
        }
    }

    /** This method auto-populates details from getSelectedAppointment on the table to the correct field.
     @param event button click
     @throws SQLException
     */
    @FXML
    void MOD_appt(ActionEvent event) throws SQLException {
        //get selected table data
        if(MODapptTableView.getSelectionModel().getSelectedItem() != null){
            Appt selectedAppt = MODapptTableView.getSelectionModel().getSelectedItem();
            getSelectedAppointment(selectedAppt);
        } else {
            //alert if no customer selected.
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select appointment to update.");
            alert.showAndWait();
        }
    }



    /** This method auto-populates details from selectedAppt on the MainScreen to the modify screen and set the correct fields.
     @param selectedAppt
     @throws SQLException
     */
    public void getSelectedAppointment (Appt selectedAppt) throws SQLException {
        this.selectedAppt = selectedAppt;
        for(Contacts contact: contactCBOX.getItems()){
            if(contact.getContactID()== selectedAppt.getContactID()){
                contactCBOX.setValue(contact);
                break;
            }
        }
        for(Customer customer: custCBOX.getItems()){
            if(customer.getCustomerId()== selectedAppt.getCustId()){
                custCBOX.setValue(customer);
                break;
            }
        }
        for(User user: userCBOX.getItems()){
            if(user.getUserID()== selectedAppt.getUserID()){
                userCBOX.setValue(user);
                break;
            }
        }
        for(Appt appt : allAppts){
            if(selectedAppt.getApptType().equals(selectedAppt.getApptType())){
                apptType_CBOX.getSelectionModel().select(String.valueOf(appt));
                break;
            }
        }
        //set fields
        apptId_txtFld.setText(Integer.toString(this.selectedAppt.getApptId()));
        title_txtFld.setText(this.selectedAppt.getApptTitle());
        desc_txtFld.setText(this.selectedAppt.getDesc());
        loc_txtFld.setText(this.selectedAppt.getLoc());
        apptType_CBOX.setValue(this.selectedAppt.getApptType());
        LocalDate date = this.selectedAppt.getApptStart().toLocalDate();
        LocalTime start = this.selectedAppt.getApptStart().toLocalTime();
        LocalTime end = this.selectedAppt.getApptEnd().toLocalTime();
        DatePicker.setValue(date);
        startCBOX.setValue(start);
        endCBOX.setValue(end);
    }


    /**
     * Initialize and populate combo-boxes. Create time conversion for the Start/End combo-boxes.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<User> allUsers = dbQuery.getAllUsers();
            userCBOX.setItems(allUsers);
            ObservableList<Customer> allCustomersNameID = dbQuery.getCustID();
            custCBOX.setItems(allCustomersNameID);
            ObservableList<Contacts> allContacts = dbQuery.getAllContacts();
            contactCBOX.setItems(allContacts);
        } catch (SQLException e) {
            System.out.println("Error: "+e);
        }
        //populate Types combo-box
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting","Customer Meeting","Review Meeting","De-Briefing");
        apptType_CBOX.setItems(appointmentTypes);


        //Set TableView with DB info
        MODapptTableView.setItems(allAppts);
        apptID_col.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        custID_col.setCellValueFactory(new PropertyValueFactory<>("custId"));
        title_col.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        desc_col.setCellValueFactory(new PropertyValueFactory<>("desc"));
        contact_col.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        loc_col.setCellValueFactory(new PropertyValueFactory<>("loc"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        start_col.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        end_col.setCellValueFactory(new PropertyValueFactory<>("apptEnd"));
        try {
            ObservableList<User> allUsers = dbQuery.getAllUsers();
            userCBOX.setItems(allUsers);
            ObservableList<Customer> allCustomersNameID = dbQuery.getCustID();
            custCBOX.setItems(allCustomersNameID);
            ObservableList<Contacts> allContacts = dbQuery.getAllContacts();
            contactCBOX.setItems(allContacts);
        } catch (SQLException e) {
            System.out.println("Error: "+e);
        }

        //fill Start/End times combo-box
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
            startCBOX.getItems().add(first);
            endCBOX.getItems().add(last);
            first = first.plusMinutes(30);
            last = first.plusMinutes(30);

        }

    }
}
