package Main;

/** @author Jonathan Fletcher */

import DBUtilities.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

/** main class for the MAIN application.
 * JavaDocs location: C195-Jonathan Fletcher\Javadocs
 * */
public class Main extends Application {

    /**
     * The start method presents user with the Login screen
     * @param primaryStage Initial stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../Windows/LoginPage.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    /***
     * Set default language and open the DB connection. Close connection upon program exit.
     * @param args arguements
     */
    public static void main(String[] args) {
        ResourceBundle rb = ResourceBundle.getBundle("Translations/languages", Locale.getDefault());
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}


