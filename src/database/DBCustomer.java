
package database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static javafx.collections.FXCollections.observableArrayList;
import models.Contact;
import models.Country;
import models.Customer;
import models.State;

/**
 * Handles the customer operations interactions with the database
 * @author Matthew Leyden
 */
public class DBCustomer {
    
    // customers list is populated with customers currently in the database and
    // is consistently updated to reflect current data.
    public static ObservableList<Customer> customers = observableArrayList();
    

    /**
     * Inserts a new customer into the database
     * <p>Retrieves customer back from database into Customer object and places into customers List</p>
     * @param name Name of the customer
     * @param address Address of the customer
     * @param phone Phone number of the customer
     * @param zip Postal code of the customer
     * @param divisionId Division ID of the customer
     */
    public static void saveNewCustomer(String name, String address, String phone, String zip,
            int divisionId) throws SQLException{
        try {
            String insertStatement = String.format("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone,  Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
            "VALUES('%s', '%s', '%s', '%s', NOW(), '%s', NOW(), '%s', %d)", name, address, zip, phone, DBLogin.getMainUser().getUsername(), DBLogin.getMainUser().getUsername(), divisionId);
            Statement statement = DBQuery.getStatement();
            statement.execute(insertStatement);

            String retrieveStatement = "SELECT * FROM customers ORDER BY Customer_ID DESC LIMIT 1";
            Statement statement2 = DBQuery.getStatement();
            statement2.execute(retrieveStatement);
            ResultSet rs = statement.getResultSet();
            
            while (rs.next()){
              
            Customer customer = new Customer(
                rs.getInt("Customer_ID"), 
                rs.getString("Customer_Name"), 
                rs.getString("Address"),
                rs.getString("Postal_Code"),
                rs.getString("Phone"),
                rs.getInt("Division_ID"));
            
            // The setupState method of the Customer class is used to retrieve the
            // state and country associated with the Customer for future reference.
            customer.setupState();
            customers.add(customer);}
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates an existing customer in the database.
     * @param s The customer class to be updated
     */
    public static void updateOldCustomer(Customer s){
        try {
            String updateStatement = String.format("UPDATE customers SET Customer_Name='%s',"
            + " Address='%s', Postal_Code='%s', Phone='%s', Last_Update=NOW(), Last_Updated_By='%s', Division_ID=%d WHERE Customer_ID=%d", s.getName(), 
            s.getAddress(), s.getZip(), s.getPhone(), DBLogin.getMainUser().getUsername(), s.getDivisionId(), s.getId());
            Statement statement = DBQuery.getStatement();
            statement.execute(updateStatement);
        } catch(SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
    /**
     * Delete Customer from Database
     * <p>first remove customer from customers list to keep everything up to date</p>
     * <p>Delete the customers appointments from database and appointments List</p>
     * <p>Remove customer record from database</p>
     * @param selectedCustomer The Customer object to be deleted
     */
    public static void deleteCustomer(Customer selectedCustomer){
        customers.remove(selectedCustomer);
        
        try {
   
            String deleteAppointments = String.format("DELETE FROM appointments WHERE Customer_ID=%d", selectedCustomer.getId());
            Statement statement = DBQuery.getStatement();
            statement.execute(deleteAppointments);
            if (DBAppointment.appointments.isEmpty()){
        
                DBAppointment.loadAppointments();
                DBAppointment.appointments = DBAppointment.appointments.filtered(a -> a.getCustomerId() != selectedCustomer.getId());
            } else {
                DBAppointment.appointments = DBAppointment.appointments.filtered(a -> a.getCustomerId() != selectedCustomer.getId());
            }
            
            String deleteCustomer = String.format("DELETE FROM customers WHERE Customer_ID=%d", selectedCustomer.getId());
            statement.execute(deleteCustomer);
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Load all the customers currently in the database 
     * <p>Query database and load information into Customer objects</p>
     * <p>Place Customer object into customers list</p>
     * @return Observable List of customer classes
     */
    public static ObservableList<Customer> loadCustomers() throws SQLException{
     
        String selectStatement = "SELECT * FROM customers";

        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        
        ResultSet rs = statement.getResultSet();
   
        while (rs.next()){
              
                Customer customer = new Customer(
                    rs.getInt("Customer_ID"), 
                    rs.getString("Customer_Name"), 
                    rs.getString("Address"),
                    rs.getString("Postal_Code"),
                    rs.getString("Phone"),
                    rs.getInt("Division_ID"));
                  
                customers.add(customer);
                
        }
        for (Customer customer : customers)
            customer.setupState();
        return customers;
    }
    
    /**
     * Used to associate a State with a particular customer
     * @param divisionId The state/province ID used to query the database
     * @return A state object for customer
     */
    public static State getState(int divisionId) throws SQLException{
        State state = new State();
        String selectStatement = String.format("SELECT * FROM first_level_divisions WHERE Division_ID=%d", divisionId);
        
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        ResultSet rs = statement.getResultSet();
   
        while (rs.next()){
            state.setDivisionId(rs.getInt("Division_ID"));
            state.setState(rs.getString("Division"));
            state.setCountryId(rs.getInt("Country_ID"));}
                  
        return state;
    }

    /**
     * Prepopulates the countries in the country combobox on the AddNewCustomer and UpdateCustomer screens
     * @return ObservableList of country classes
     */
    public static ObservableList getCountries() {

        ObservableList<Country> countries = FXCollections.observableArrayList();
        String selectStatement = "SELECT * FROM countries";
        try {
            Statement statement = DBQuery.getStatement();
            statement.execute(selectStatement);
        
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                int countryId = rs.getInt("Country_ID");
                String country = rs.getString("Country");
                Country c = new Country(countryId, country);
                countries.add(c);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    /**
     * Used to get first_level_division data for user-chosen country to prepopulate the state combobox
     * @param countryId ID of the country used to query the database
     * @return Observable list of state classes
     */
    public static ObservableList getCitiesForCountry(int countryId){
        
        ObservableList<State> states = FXCollections.observableArrayList();
        String selectStatement = String.format("SELECT * FROM first_level_divisions WHERE COUNTRY_ID = %d", countryId);
        try {
            Statement statement = DBQuery.getStatement();
            statement.execute(selectStatement);
        
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
               State state = new State(rs.getInt("division_ID"), rs.getString("Division"), countryId);
               states.add(state);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return states;
    }
}

