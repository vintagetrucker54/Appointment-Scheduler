package Models;

/** @author Jonathan Fletcher */

import java.time.LocalDate;
import java.time.LocalDateTime;
import DBUtilities.DBQuery;
import javafx.collections.ObservableList;
import java.sql.SQLException;

/**
 * Model for appointment objects.
 */
public class Appt {

    private int apptId;
    private String apptTitle;
    private String desc;
    private String loc;
    private String apptType;
    private LocalDateTime apptStart;
    private LocalDateTime apptEnd;
    private LocalDate date;
    private int custId;
    private String custName;
    private int userID;
    private String userName;
    private int contactID;
    private String contactName;

    /**
     * Constructor for a new appointment.
     * @param apptId unique appointment ID
     * @param apptTitle title of appointment
     * @param desc appointment description
     * @param loc location of appointment
     * @param apptType type of established appointments
     * @param apptStart start time of appointments
     * @param apptEnd end time of appointments
     * @param custId unique customer ID
     * @param custName ID of established/unique customers
     * @param userID ID os established users
     * @param userName Name of established users
     * @param contactID ID of established contacts
     * @param contactName Name of established contacts
     */
    public Appt(int apptId, String apptTitle, String desc, String loc, String apptType, LocalDateTime apptStart, LocalDateTime apptEnd, int custId, String custName, int userID, String userName, int contactID, String contactName) {
        this.apptId = apptId;
        this.apptTitle = apptTitle;
        this.desc = desc;
        this.loc = loc;
        this.apptType = apptType;
        this.apptStart = apptStart;
        this.apptEnd = apptEnd;
        this.date = this.apptStart.toLocalDate();
        this.custId = custId;
        this.custName = custName;
        this.userID = userID;
        this.userName = userName;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * empty constructor
     */
    public Appt() {}

    //GETTERS
    /** @return current appointment ID as int. */
    public int getApptId() {
        return apptId;
    }

    /** @return customer ID as int. */
    public int getCustId() {
        return custId;
    }

    /** @return user ID as int. */
    public int getUserID() {
        return userID;
    }

    /** @return appointment title as string. */
    public String getApptTitle() {
        return apptTitle;
    }

    /** @return appointment description as string. */
    public String getDesc() {
        return desc;
    }

    /** @return appointment location as string. */
    public String getLoc() {
        return loc;
    }

    /** @return int contact ID. */
    public int getContactID() {
        return contactID;
    }

    /** @return selected appointment type. */
    public String getApptType() {
        return apptType;
    }

    /** @return start date/time of appointment. */
    public LocalDateTime getApptStart() {
        return apptStart;
    }

    /** @return date of appointment. */
    public LocalDate getDate() {
        return date;
    }

    /** @return current appointment contact name as String. */
    public String getContactName() {
        return contactName;
    }

    /** @return end date/time of appointment. */
    public LocalDateTime getApptEnd() {
        return apptEnd;
    }




    //SETTERS
    /** @param userID user ID to set. */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /** @param loc appointment location to set. */
    public void setLoc(String loc) {
        this.loc = loc;
    }

    /** @param date sets appointment date. */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** @param apptStart start date/time of appointment to set. */
    public void setApptStart(LocalDateTime apptStart) {
        this.apptStart = apptStart;
    }

    /** @param contactName contactName to set. */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }




    /**
     * This method checks for conflicts with existing appointments before allowing users to create/update appointments.
     * @param custId unique customer ID
     * @param apptStart start time of appointment
     * @param apptEnd end time of appointment
     * @return conflicting appointment
     * @throws SQLException
     */
    public static Appt overlap(int custId, LocalDateTime apptStart, LocalDateTime apptEnd) throws SQLException {
        Appt apptConflict = null;
        DBQuery dbQuery = new DBQuery();
        ObservableList<Appt> allCustomerAppts = dbQuery.getCustAppts(custId);

        for (Appt appt : allCustomerAppts) {
            if(apptStart.isAfter(appt.getApptStart()) && apptStart.isBefore(appt.getApptEnd()) ||
            apptEnd.isAfter(appt.getApptStart()) && apptEnd.isBefore(appt.getApptEnd()) ||
            apptStart.isBefore(appt.getApptStart()) && apptEnd.isAfter(appt.getApptEnd()) ||
            apptStart.equals(appt.getApptStart()) && apptEnd.equals(appt.getApptEnd()) ||
            apptStart.equals(appt.getApptStart()) || apptEnd.equals(appt.getApptEnd())) {
                apptConflict = appt;
            }
        }
        return apptConflict;
    }


  
}
