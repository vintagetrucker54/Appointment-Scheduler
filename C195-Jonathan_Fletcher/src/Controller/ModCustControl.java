package Controller;

/** @author Jonathan Fletcher */

import Models.Countries;
import Models.Customer;
import Models.firstLevelDivision;
import DBUtilities.DBQuery;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Modify Customer Screen.
 */
public class ModCustControl implements Initializable {

    @FXML private TextField custId_txtFld;
    @FXML private TextField custName_txtFld;
    @FXML private TextField address_txtFld;
    @FXML private TextField pCode_txtFld;
    @FXML private TextField phone_txtFld;

    @FXML private ComboBox<firstLevelDivision> state_CBOX;
    @FXML private ComboBox<Countries> country_CBOX;

    @FXML private Label msg_lbl;
    @FXML private Label choose_lbl;

    @FXML private TableView<Customer> CustomersTableView;

    @FXML private TableColumn<Customer, Integer> CustIDCol;
    @FXML private TableColumn<Customer, String> CustNameCol;
    @FXML private TableColumn<Customer, String> CustAddressCol;
    @FXML private TableColumn<firstLevelDivision, String> CustCityCol;
    @FXML private TableColumn<Countries, String> CustCountryCol;
    @FXML private TableColumn<Customer, String> CustPostalCodeCol;

    Customer selectedCustomer;
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
     * This method loads main Appointment Screen when the 'return to...' button clicked.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void Cancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel?");
        Optional<ButtonType> result = alert.showAndWait();
        returnToMain(event);
        }


    /** This method auto-populates details from getSelectedCustomer on the table to the correct field.
     @param event button click
     @throws SQLException
     */
    @FXML
    void MOD_cust(ActionEvent event) throws SQLException {
        //get selected table data
        if(CustomersTableView.getSelectionModel().getSelectedItem() != null){
            Customer selectedCustomer = CustomersTableView.getSelectionModel().getSelectedItem();
            getSelectedCustomer(selectedCustomer);
        } else {
            //alert if no customer selected.
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to update.");
            alert.showAndWait();
        }
    }


    /** This method auto-populates details from selectedCustomer on the MainScreen to the modify screen and set the correct fields.
     @param selectedCustomer
     @throws SQLException
     */
    public void getSelectedCustomer(Customer selectedCustomer) throws SQLException {
        this.selectedCustomer = selectedCustomer;
        ObservableList<firstLevelDivision> allStates = dbQuery.getAllStates();
        ObservableList<Countries> allCountries = dbQuery.getAllCountries();
        for(Countries c : allCountries){
            if(selectedCustomer.getCountry().equals(c.getCountry())){
                country_CBOX.getSelectionModel().select(c);
                break;
            }
        }
        for(firstLevelDivision fld : allStates){
            if(selectedCustomer.getDivision().equals(fld.getDivision())){
                state_CBOX.getSelectionModel().select(fld);
                break;
            }
        }
        //set fields
        state_CBOX.setItems(allStates);
        country_CBOX.setItems(allCountries);
        custId_txtFld.setText(Integer.toString(this.selectedCustomer.getCustomerId()));
        custName_txtFld.setText(this.selectedCustomer.getCustomerName());
        address_txtFld.setText(this.selectedCustomer.getAddress());
        pCode_txtFld.setText(this.selectedCustomer.getPostalCode());
        phone_txtFld.setText(this.selectedCustomer.getPhone());
    }



    /**
     * Lambda expression used to filter the list and increase readability.
     * This method filters the list of 'first-level-divisions' according to the country chosen.
     * @param event
     * @throws SQLException
     * <p><b> 3D: Lambda Justification - Lambda expression used to filter the list and increase readability. </b></p>
     */
    @FXML
    void countrySelect(ActionEvent event) throws SQLException {
        ObservableList<firstLevelDivision> allStates = dbQuery.getAllStates();
        int countryID = country_CBOX.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<firstLevelDivision> fldList = allStates.filtered(fld -> fld.getCountryID() == countryID);
        state_CBOX.setItems(fldList);
    }


    /**
     * This method allows appointment delete from DB. It first deletes associated appointments,
     * then customer, then returns to main Appointment screen.
     * @param event button click
     * @throws IOException
     */
    @FXML
    void DEL_cust(ActionEvent event) throws IOException {
        Customer selectedCustomer = CustomersTableView.getSelectionModel().getSelectedItem();
        int customerId = selectedCustomer.getCustomerId();
        int apptId = customerId;
        String customerName = selectedCustomer.getCustomerName();

        if(selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Delete Customer and associated appointments?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dbQuery.deleteCustomer(apptId,customerId);
                msg_lbl.setText(customerName + " deleted.");
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer was deleted.");
                result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                   returnToMain(event);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to delete.");
            alert.showAndWait();
        }
    }

    /**
     * This method allows customer update after validations.
     * @param event button click
     */
    @FXML
    void SAVE_update(ActionEvent event) {
        //Ensure no empty fields
        try {
            if(!custName_txtFld.getText().isEmpty() && !address_txtFld.getText().isEmpty() && !pCode_txtFld.getText().isEmpty() && !phone_txtFld.getText().isEmpty() && state_CBOX.getValue() != null && country_CBOX.getValue() != null){
                int customerId = Integer.parseInt(custId_txtFld.getText());
                String customerName = custName_txtFld.getText();
                String customerAddress = address_txtFld.getText();
                firstLevelDivision customerFLD = state_CBOX.getValue();
                String customerPostalCode= pCode_txtFld.getText();
                String customerPhone = phone_txtFld.getText();
                int intCustomerFLD = dbQuery.getCustFLD(customerFLD);
                dbQuery.updateCust(customerName, customerAddress, customerPostalCode, customerPhone, intCustomerFLD, customerId);

                msg_lbl.setText(customerName + " updated.");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer updated!");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    returnToMain(event);
                }
            }  else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Must enter a valid value for each text field.");
                alert.showAndWait();
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("Error: "+ e);
        }
    }


    /**
     * Initialize and populates the customers table.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
            CustomersTableView.setItems(allCustomers);
        } catch (SQLException e) {
            System.out.println("Error: "+ e);
        }
        CustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        CustNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        CustCityCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        CustCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        CustPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
    }
}
