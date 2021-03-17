package Controller;
/** @author Jonathan Fletcher */

import DBUtilities.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Controller for the Appointments Screen.
 */
public class LoginPageControl implements Initializable {

    @FXML private Label Header_lbl;
    @FXML private Label login_msg;
    @FXML private Label zone_lbl;
    @FXML private Label userName_lbl;
    @FXML private Label password_lbl;

    @FXML private TextField userName_txtFld;
    @FXML private PasswordField password_txtFld;

    @FXML private Button login_BTN;

    ResourceBundle rb = ResourceBundle.getBundle("Translations/languages", Locale.getDefault());


    /**
     * Verifies username and password with the DB 'customers' table,
     * Record log-in attempt in login_attempts.txt. On successful log in, opens Appointments screen.
     * @param event button click
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    void LogIn(ActionEvent event) throws IOException, SQLException {
        String username = userName_txtFld.getText();
        String password = password_txtFld.getText();
        boolean isValid = validation(username, password);

        //write log in attempts to txt file
        FileWriter fw = new FileWriter("login_activity.txt", true);
        BufferedWriter logger = new BufferedWriter(fw);
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());

        //used for lang changes
        String success = rb.getString("LoginStatusSuccess");
        String error = rb.getString("LoginStatusFailed");

        if(isValid){
            login_msg.setText(success);
            logger.write(username + " successfully logged in at: " + loginTime);
            logger.newLine();
            logger.close();

            //load Appointments screen if valid log in
            Stage stage;
            Parent root;
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Windows/Appointment.fxml"));
            stage.setTitle("Appt Management System");
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ApptControl controller = loader.getController();
            controller.upcomingAppts(username);
        } else {
            login_msg.setText(error);
            logger.write(username + " - unsuccessful at: " + loginTime);
            logger.newLine();
            logger.close();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(error);
            alert.showAndWait();
          }
    }


    /**
     * This method validates the username & password combos against stored DB info.
     * @param username DB username
     * @param password DB password
     * @return boolean if both parameters are true or not
     */
    public boolean validation(String username, String password){
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT * FROM users WHERE User_Name ='" + username + "' AND Password ='" + password + "'";
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return false;
        }
    }


    /**
     * This method sets text for labels on the login screen to switch btw langs.
     * Stored in resource bundles for partial program internationalization.
     */
    @FXML
    public void setTextLabels() {
        Header_lbl.setText(rb.getString("LoginFormHeaderLabel"));
        login_BTN.setText(rb.getString("LoginButton"));
        login_msg.setText(rb.getString("LoginStatus"));
        zone_lbl.setText(rb.getString("ZoneLabel"));
        userName_lbl.setText(rb.getString("UsernameLabel"));
        password_lbl.setText(rb.getString("PasswordLabel"));
    }


    /**
     * Initializes Login controller.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String string = ZoneId.systemDefault().toString();
        zone_lbl.setText("Location: " + string);
        setTextLabels();
    }
}

