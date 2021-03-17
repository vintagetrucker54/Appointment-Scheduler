package Models;
/** @author Jonathan Fletcher */
import DBUtilities.DBQuery;
import javafx.collections.ObservableList;
import java.sql.SQLException;

/**
 *  Model for Country objects.
 */
public class Customer {

    private final int customerId;
    private final String customerName;
    private final String address;
    private final String postalCode;
    private String phone;
    private final int divisionId;
    private final String country;
    private final String division;


    /**
     * Constructor for new customer objects.
     * @param customerId unique ID of customer
     * @param customerName Name of customer
     * @param address customer address
     * @param postalCode postal code
     * @param phone phone number
     * @param divisionId FLD ID
     * @param division FLD
     * @param country country
     */
    public Customer(int customerId, String customerName, String address, String postalCode, String phone, int divisionId, String division, String country) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
        this.division = division;
        this.country = country;
    }

    /** @return country name. */
    public String getCountry() {
        return country;
    }

    /** @return first-level-division name. */
    public String getDivision() {
        return division;
    }

    /** @return customer name. */
    public String getCustomerName() {
        return customerName;
    }

    /** @return customer address as string. */
    public String getAddress() {
        return address;
    }

    /** @return string customer postal code. */
    public String getPostalCode() {
        return postalCode;
    }

    /** @return customer id as int. */
    public int getCustomerId() {
        return customerId;
    }

    /** @return customer phone number as string. */
    public String getPhone() {
        return phone;
    }

    /** @param phone set customer phone number. */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Overrides standard 'toString' method.
     * @return string in a user-friendly format
     */
    @Override
    public String toString() {
        return "[" + customerId + "] " + customerName;
    }


    /**
     * Method to get all customers in DB
     * @return All customers
     * @throws SQLException
     */
    public ObservableList<Customer> getAllCustomers() throws SQLException {
        DBQuery dbQuery = new DBQuery();
        ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
        return allCustomers;
    }
}
