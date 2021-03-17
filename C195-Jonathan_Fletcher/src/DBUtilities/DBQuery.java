package DBUtilities;

/** @author Jonathan Fletcher */

import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Main class for the majority of DB queries.
 * */
public class DBQuery {

    private PreparedStatement preparedStatement;

    /***
     * This method creates a statement object for receiving a connection.
     * @param connection DB connection
     * @param sqlStatement standard prepared sql statement
     * @throws SQLException
     */
    public void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sqlStatement);
    }


    /**
     * Method uses FLD table to find and return Division_ID.
     *
     * @param custFLD FLD associated with customer
     * @return div_ID Integer to link FLDs
     */
    public int getCustFLD(firstLevelDivision custFLD) {
        int div_ID = 0;
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + custFLD + "'";
            ResultSet rs = statement.executeQuery(sql);
            //run through FLD list
            while (rs.next()) {
                div_ID = rs.getInt("Division_ID");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return div_ID;
    }


    /**
     * Method uses customerID to return customerName
     *
     * @param custId ID integer associated with a customer
     * @return customer name
     */
    public Customer getCustName(int custId) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID WHERE Customer_ID='" + custId + "'";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String custName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String pCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                String country = rs.getString("Country");
                Customer customer = new Customer(custId, custName, address, pCode, phone, divID, division, country);

                statement.close();
                return customer;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    /**
     * Method uses username to return a user object where needed.
     *
     * @param username current users username
     * @return logged user
     */
    public User getUsername(String username) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT * FROM users WHERE User_Name='" + username + "'";
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("User_Name"));
                user.setUserID(rs.getInt("User_ID"));
                statement.close();
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    /**
     * Method to get all Customers from the DB.
     *
     * @return All customers joined by their IDs
     * @throws SQLException
     */
    public ObservableList<Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
                int custId = rs.getInt("Customer_ID");
                String custName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String pCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divId = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                String country = rs.getString("Country");
                Customer customer = new Customer(custId, custName, address, pCode, phone, divId, division, country);

                allCustomers.add(customer);
            }
            return allCustomers;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    /**
     * This method uses customers name and id to return an observableList of customers.
     * Used separately from getAllCustomers method.
     * @return All customers joined by their IDs
     * @throws SQLException
     */
    public ObservableList<Customer> getCustID() throws SQLException {
        ObservableList<Customer> allCustomersNameID = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            allCustomersNameID.add(new Customer(rs.getInt("Customer_ID"),
            rs.getString("Customer_Name"),
            rs.getString("Address"),
            rs.getString("Postal_Code"),
            rs.getString("Phone"),
            rs.getInt("Division_ID"),
            rs.getString("Division"),
            rs.getString("Country")));
        }
        ps.close();
        return allCustomersNameID;
    }

    /**
     * This method creates a new customer and inserts into the DB.
     * Returns auto-generated customer id.
     * @param Customer_Name name of customer
     * @param Address address of customer
     * @param Postal_Code postal code
     * @param Phone phone number
     * @param Division_ID FLD int
     * @return CustID unique ID for new customer
     */
    public int createCust(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID) {
        int CustID = 0;
        try {
            String sql = "INSERT INTO customers VALUES (NULL, ?, ?, ?, ?, NULL, NULL, NULL, NULL, ?)";

            PreparedStatement pscc = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //set new cust info
            pscc.setString(1, Customer_Name);
            pscc.setString(2, Address);
            pscc.setString(3, Postal_Code);
            pscc.setString(4, Phone);
            pscc.setInt(5, Division_ID);

            pscc.execute();

            ResultSet rs = pscc.getGeneratedKeys();
            rs.next();//index iteration

            CustID = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return CustID;
    }


    /**
     * This method updates an existing customer.
     * @param Customer_Name name of customer
     * @param Address address of customer
     * @param Postal_Code postal code
     * @param Phone phone number
     * @param Division_ID FLD int
     * @param Customer_ID customers unique ID
     */
    public void updateCust(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID, int Customer_ID) {
        try {
            String sql = "UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Division_ID=? WHERE Customer_ID= ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            //set new cust info
            ps.setString(1, Customer_Name);
            ps.setString(2, Address);
            ps.setString(3, Postal_Code);
            ps.setString(4, Phone);
            ps.setInt(5, Division_ID);
            ps.setInt(6, Customer_ID);

            ps.execute();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Method to retrieve 'first-level-divisions'(states/provinces).
     * @return all 'first level divisions" as set in the ERD
     * @throws SQLException
     */
    public ObservableList<firstLevelDivision> getAllStates() throws SQLException {
        ObservableList<firstLevelDivision> allStates = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            allStates.add(new firstLevelDivision(rs.getInt("Country_ID"), rs.getString("Division")));
        }
        ps.close();
        return allStates;
    }

    /**
     * This method gets all countries.
     * @return all countries as set in the ERD
     * @throws SQLException
     */
    public ObservableList<Countries> getAllCountries() throws SQLException {
        ObservableList<Countries> allCountries = FXCollections.observableArrayList();
        String sql = "SELECT * FROM countries";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery(sql);
        while (resultSet.next()) {
            allCountries.add(new Countries(resultSet.getString("Country"), resultSet.getInt("Country_ID")));
        }
        ps.close();
        return allCountries;
    }

    /**
     * This method gets all Contacts.
     * @return all contacts as set in the ERD
     * @throws SQLException
     */
    public ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> allContacts = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            allContacts.add(new Contacts(rs.getInt("Contact_ID"), rs.getString("Contact_Name"), rs.getString("Email")));
        }
        ps.close();
        return allContacts;
    }

    /**
     * This method gets all Users.
     * @return all users as set in the ERD
     * @throws SQLException
     */
    public ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            allUsers.add(new User(rs.getInt("User_ID"), rs.getString("User_Name")));
        }
        ps.close();
        return allUsers;
    }

    /**
     * Method to delete a customer using the customer ID. Deletes appointments associated with customer first before customer.
     * @param apptId appointment ID from specific customer
     * @param customerId customer ID from a specific customer
     */
    public void deleteCustomer(int apptId, int customerId) {
        //delete all associated appointments FIRST, then the customer
        try {
            String aSql = "DELETE FROM appointments WHERE Customer_ID = ?";
            String cSql = "DELETE FROM customers WHERE Customer_ID=?";
            PreparedStatement ps1 = DBConnection.getConnection().prepareStatement(aSql);
            PreparedStatement ps2 = DBConnection.getConnection().prepareStatement(cSql);
            ps1.setInt(1, apptId);
            ps2.setInt(1, customerId);
            ps1.execute();
            ps2.execute();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Method for deleting selected appointment.
     * @param apptId appointment ID from specific customer
     */
    public void deleteAppt(int apptId) { //parameter is in both tables
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, apptId);
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }


    /**
     * This method creates an appointment.
     * @param Title appointment title
     * @param Description description of appointment
     * @param Location appointment location
     * @param Type appointment type from established list
     * @param Start start time of appointments
     * @param End end time of appointments
     * @param customerId unique customer ID
     * @param userId ID of established users
     * @param contactId ID of established contacts
     * @return apptID - unique ID
     */
    public int createAppt(String Title, String Description, String Location, String Type,
                          LocalDateTime Start, LocalDateTime End, int customerId, int userId, int contactId) {
        int apptID = 0;
        try {
            String sql = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //set new appointment info
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Location);
            ps.setString(4, Type);
            ps.setTimestamp(5, Timestamp.valueOf(Start));
            ps.setTimestamp(6, Timestamp.valueOf(End));
            ps.setInt(7, customerId);
            ps.setInt(8, userId);
            ps.setInt(9, contactId);

            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();//index iteration
            apptID = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return apptID;
    }

    /**
     * This method updates an appointment.
     * @param apptID unique appointment ID
     * @param apptTitle title of appointment
     * @param desc description of appointment
     * @param loc appointment location
     * @param apptType established type of appointmet
     * @param apptStart start time of appointment
     * @param apptEnd end time of the appointment
     * @param custID unique customer ID
     * @param userID ID of established User
     * @param contactID ID of established contact
     * @throws SQLException
     */
    public void updateAppt(int apptID, String apptTitle, String desc, String loc, String apptType,
                           LocalDateTime apptStart, LocalDateTime apptEnd, int custID, int userID, int contactID) throws SQLException {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        //set appointment info
        ps.setString(1, apptTitle);
        ps.setString(2, desc);
        ps.setString(3, loc);
        ps.setString(4, apptType);
        ps.setTimestamp(5, Timestamp.valueOf(apptStart));
        ps.setTimestamp(6, Timestamp.valueOf(apptEnd));
        ps.setInt(7, custID);
        ps.setInt(8, userID);
        ps.setInt(9, contactID);
        ps.setInt(10, apptID);

        ps.execute();
    }


    /**
     * This method gets all appointments.
     * @return  appointment from specific customer
     */
    public ObservableList<Appt> getAllAppts() {
        ObservableList<Appt> allAppts = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                allAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return allAppts;
    }

    /**
     * Method to get all appointments. Does not get future or past months, only current month.
     * @return appointment(s) in current month according to default Locale
     * @param monthInt integer of current month
     */
    public static ObservableList<Appt> getMonthlyAppts(int monthInt) {
        int month = LocalDate.now().getMonthValue();
        ObservableList<Appt> monthlyAppts = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID AND month(start) = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                monthlyAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
            ps.close();
            return monthlyAppts;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }


    /**
     * Method to get all appointments. Does not get future or past weeks, only current week.
     * @param date integer of current week
     * @return appointment(s) in current week according to default Locale
     */
    public ObservableList<Appt> getWeeklyAppts(LocalDate date) {
        LocalDateTime start = LocalDateTime.now();
        while (start.getDayOfWeek() != DayOfWeek.SUNDAY) {
            start = start.minusDays(1);
        }
        Timestamp startTime = Timestamp.valueOf(start);
        Timestamp endTime = Timestamp.valueOf(start.plusWeeks(1));
        ObservableList<Appt> weeklyAppts = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID AND Start >= ?  AND Start <= ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                weeklyAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
            ps.close();
            return weeklyAppts;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }



    /**
     * This method gets all appointments for one customer from DB, using their ID.
     * @param customerId unique customer ID
     * @return customer's associated appointments
     */
    public ObservableList<Appt> getCustAppts(int customerId) {
        ObservableList<Appt> custAppts = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.Customer_ID = '" + customerId + "'";

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                custAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
            statement.close();
            return custAppts;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    /**
     * This method gets all appointments for one contact from DB, using their ID.
     * @param contactId ID of established contacts
     * @return info of contacts
     */
    public ObservableList<Appt> getApptContacts(int contactId) {
        ObservableList<Appt> contactAppts = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.Contact_ID='" + contactId + "'";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                contactAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
            statement.close();
            return contactAppts;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }


    /**
     * This method gets all appointments for one user from DB, using their ID.
     * @param userId ID of established users
     * @return info of users
     */
    public ObservableList<Appt> getUserAppts(int userId) {
        ObservableList<Appt> userAppts = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.User_ID='" + userId + "'";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                userAppts.add(new Appt(rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime(),
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name")));
            }
            statement.close();
            return userAppts;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}


