package Controller;

/** @author Jonathan Fletcher */

import Models.Countries;
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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the New Customer Screen.
 */
public class NewCustControl implements Initializable {

    @FXML private TextField pCode_txtFld;
    @FXML private TextField phone_txtFld;
    @FXML private TextField custName_txtFld;
    @FXML private TextField address_txtFld;

    @FXML private ComboBox<firstLevelDivision> state_CBOX;
    @FXML private ComboBox<Countries> country_CBOX;

    @FXML private Label msg_lbl;

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
     @throws IOException
     */
    @FXML
    void Save(ActionEvent event) throws IOException {
        try {
            if(!custName_txtFld.getText().isEmpty() && !address_txtFld.getText().isEmpty() && !pCode_txtFld.getText().isEmpty() && !phone_txtFld.getText().isEmpty() && state_CBOX.getValue() != null && country_CBOX.getValue() != null){
                //retrieve field values
                String customerName = custName_txtFld.getText();
                String customerAddress = address_txtFld.getText();
                firstLevelDivision customerFLD = state_CBOX.getValue();
                String customerPostalCode= pCode_txtFld.getText();
                String customerPhone = phone_txtFld.getText();
                int intCustomerFLD = dbQuery.getCustFLD(customerFLD);

                dbQuery.createCust(customerName, customerAddress, customerPostalCode, customerPhone, intCustomerFLD);
                //set in-app message
                msg_lbl.setText(customerName + " successfully added.");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer added!");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    returnToMain(event);
                }
            }  else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Must enter a valid value for each text field.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: "+e);
        }
    }



    /**
     * Lambda to filter the list and increase readability.
     * This method filters the list of 'first-level-divisions' according to the country chosen.
     * @param event value selected from combo box
     * @throws SQLException
     */
    @FXML
    void countryComboBOX(ActionEvent event) throws SQLException {
        ObservableList<firstLevelDivision> allStates = dbQuery.getAllStates();
        int countryID = country_CBOX.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<firstLevelDivision> FLDlist = allStates.filtered(FLD -> FLD.getCountryID() == countryID);
        state_CBOX.setItems(FLDlist);
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
     * Initializes Customer class and populates customer info table and combo-boxes.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Countries> countries = null;
        ObservableList<firstLevelDivision> allStates = null;

        try {
            countries = dbQuery.getAllCountries();
            allStates = dbQuery.getAllStates();
        } catch (SQLException e) {
            System.out.println("Error: "+e);
        }
        country_CBOX.setItems(countries);
        state_CBOX.setItems(allStates);
   }
}
