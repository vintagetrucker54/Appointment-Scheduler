package Models;

/** @author Jonathan Fletcher */

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Model for user objects.
 */
public class User {

   private int userID;
   private String userName;
   private String userPassword;

    /**
     * Constructor for new user objects.
     * @param userID
     * @param userName
     * @param userPassword
     */
    public User(int userID, String userName, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    /**
     * Overloaded constructor
     * @param userId
     * @param userName
     */
    public User(int userId, String userName) {
        this.userID = userId;
        this.userName = userName;
    }

    /**
     * default (empty) constructor
     */
    public User(){}

    /** @return int user id. */
    public int getUserID() {
        return userID;
    }

    /** @return string username. */
    public String getUserName() {
        return userName;
    }

    /** @param userID sets userId. */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /** @param userName string userName. */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Overrides standard 'toString' method.
     * @return string in a user-friendly format
     */
    @Override
    public String toString() {
        return "[" + userID + "] " + userName;
    }

}
