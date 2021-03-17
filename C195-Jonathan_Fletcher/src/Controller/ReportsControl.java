package Controller;

/** @author Jonathan Fletcher */

import Models.*;
import DBUtilities.*;
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
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller for the Reports Screen.
 */
public class ReportsControl implements Initializable {

    @FXML private ComboBox<String> monthCBOX;
    @FXML private ComboBox<User> consultCBOX;
    @FXML private ComboBox<Customer> custCBOX;

    @FXML private TableView<Reports> apptRep_Table;
    @FXML private TableColumn<Reports, String> apptType_col;
    @FXML private TableColumn<Reports, Integer> apptCount_col;

    @FXML private TableView<Appt> custRep_Table;
    @FXML private TableColumn<Customer, Integer> custRep_apptIdCOL;
    @FXML private TableColumn<Customer, String> custRep_titleCOL;
    @FXML private TableColumn<Customer, String> custRep_typeCOL;
    @FXML private TableColumn<Customer, String> custRep_descCOL;
    @FXML private TableColumn<Customer, Integer> custRep_custIdCOL;
    @FXML private TableColumn<Customer, LocalDateTime> custRep_startCOL;

    @FXML private TableView<Appt> consultRep_Table;
    @FXML private TableColumn<Contacts, Integer> consultRep_apptIdCOL;
    @FXML private TableColumn<Contacts, String> consultRep_titleCOL;
    @FXML private TableColumn<Contacts, String> consultRep_typeCOL;
    @FXML private TableColumn<Contacts, String> consultRep_descCOL;
    @FXML private TableColumn<Contacts, Integer> consultRep_custIdCOL;
    @FXML private TableColumn<Contacts, LocalDateTime> consultRep_startCOL;

    Stage stage;
    Parent scene;
    DBQuery dbQuery = new DBQuery();



    /**
     * This method changes monthName string to int for monthMap method.
     * @param monthName hashmap values
     * @return string value of selected int from hashmap
     */
    public int getMonthInt(String monthName) {
        return monthMap.get(monthName);
    }
    /**
     * Hashmap to pair months equivalent integer; getMonthInt returns an int for monthName.
     */
    private final Map<String, Integer> monthMap = new HashMap<>(){
        {
            put("January", 1); put("February", 2); put("March", 3); put("April", 4); put("May", 5);
            put("June", 6); put("July", 7); put("August", 8); put("September", 9); put("October", 10);
            put("November", 11); put("December", 12);
        }
    };


    /**
     * This method controls the Appt Types report. Shows current and future reports (no past appointments).
     * @param event value selected from combo box
     */
    @FXML
    void getMonths(ActionEvent event) throws SQLException {
        String monthName = monthCBOX.getValue();
        int month = getMonthInt(monthName);
        ObservableList<Appt> apptsByMonth = DBQuery.getMonthlyAppts(month);
        int BoardMeet = 0, StaffMeet = 0, CustMeet = 0, ReviewMeet = 0, DeBriefing = 0;
        for (Appt appts : apptsByMonth) {

            //simple iteration for each type
            if (appts.getApptType().equals("Board Meeting")) {
                BoardMeet++;
            } if (appts.getApptType().equals("Staff Meeting")) {
                StaffMeet++;
            } if (appts.getApptType().equals("Customer Meeting")) {
                CustMeet++;
            } if (appts.getApptType().equals("Review Meeting")) {
                ReviewMeet++;
            } if (appts.getApptType().equals("Product Meeting")) {
                DeBriefing++;
            }
        }
        //add 'type' to appropriate cells
        ObservableList<Reports> apptReport = FXCollections.observableArrayList();
        apptReport.add(new Reports("Board Meeting", BoardMeet));
        apptReport.add(new Reports("Staff Meeting", StaffMeet));
        apptReport.add(new Reports("Customer Meeting", CustMeet));
        apptReport.add(new Reports("Review Meeting", ReviewMeet));
        apptReport.add(new Reports("De-Briefing", DeBriefing));
        apptRep_Table.setItems(apptReport);
        apptType_col.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        apptCount_col.setCellValueFactory(new PropertyValueFactory<>("numOfAppts"));
    }




    /**
     * This method controls the consultant report.
     * @param event value selected from combo box
     * @throws SQLException
     */
    @FXML
    void consultant_report(ActionEvent event) throws SQLException {
        //get combo-box value & assoc user
        User user = consultCBOX.getValue();
        int userId = user.getUserID();

        //set column values
        consultRep_apptIdCOL.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        consultRep_titleCOL.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        consultRep_typeCOL.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        consultRep_descCOL.setCellValueFactory(new PropertyValueFactory<>("desc"));
        consultRep_custIdCOL.setCellValueFactory(new PropertyValueFactory<>("custId"));
        consultRep_startCOL.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        consultRep_Table.setItems(dbQuery.getUserAppts(userId));
    }


    /**
     * This method controls the Customer report.
     * @param event value selected from combo box
     */
    @FXML
    void getCustSchedule(ActionEvent event) {
        //get combo-box value & assoc user
        Customer customer = custCBOX.getValue();
        int custID = customer.getCustomerId();

        //set column values
        custRep_apptIdCOL.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        custRep_titleCOL.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        custRep_typeCOL.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        custRep_descCOL.setCellValueFactory(new PropertyValueFactory<>("desc"));
        custRep_custIdCOL.setCellValueFactory(new PropertyValueFactory<>("custId"));
        custRep_startCOL.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        custRep_Table.setItems(dbQuery.getUserAppts(custID));
    }

    /**
     * The method returns to the appointments.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void NAV_to_Appts(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Return to Appointments?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/Windows/Appointment.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Exit program.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void Cancel(ActionEvent event) throws IOException {
        System.exit(0);
    }

    /**
     * Initialize and populate combo-boxes.
      * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> allMonths = FXCollections.observableArrayList(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
        ObservableList<Customer> allCustomersNameID = null;
        try {
            allCustomersNameID = dbQuery.getCustID();
            ObservableList<User> allUsers = dbQuery.getAllUsers();
            custCBOX.setItems(allCustomersNameID);
            consultCBOX.setItems(allUsers);
        } catch (SQLException e) {
            System.out.println("Error: "+e);
        }
        monthCBOX.setItems(allMonths);
    }
}
