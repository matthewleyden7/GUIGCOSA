
package database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import models.Appointment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.collections.FXCollections;
import models.Contact;
import models.Customer;


/**
 * Handles the appointment operations interactions with the database
 * @author Matthew Leyden
 */
public class DBAppointment {
    
    // appointments list is populated with appointments currently in the database and
    // is consistently updated to reflect current data. weekAppointments/monthAppointments 
    // is populated from appointments list with appointments occuring within the week, month
    public static ObservableList<Appointment> appointments = observableArrayList();
    public static ObservableList<Appointment> weekAppointments = observableArrayList();
    public static ObservableList<Appointment> monthAppointments = observableArrayList();

    
    /**
     * Save new appointment to the database
     * <p>Insert values of new appointment into database</p>
     * <p>retrieve information back from database to create new Appointment object and place into appointments List</p>
     * @param title The title of the appointment
     * @param description The description of the appointment
     * @param location The location of the appointment
     * @param type The type of appointment
     * @param startUTC A String that includes the start date and start time in Universal Coordinated Time
     * @param endUTC A String that includes the end date and end time in Universal Coordinated Time
     * @param customerId The integer ID of the customer
     * @param contactId The integer ID of the contact
     */
    public static void saveNewAppointment(String title, String description, String location, String type, String startUTC, String endUTC, int customerId, int contactId) throws SQLException{
        
        try {
            String insertStatement = String.format("INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', NOW(), '%s', NOW(), '%s', %d, %d, %d)", title, description, location, type, startUTC, endUTC, DBLogin.getMainUser().getUsername(), DBLogin.getMainUser().getUsername(), customerId, DBLogin.getMainUser().getUserId(), contactId);
            Statement statement = DBQuery.getStatement();
            statement.execute(insertStatement);
            
            String retrieveStatement = "SELECT * FROM appointments ORDER BY Appointment_ID DESC LIMIT 1";
            Statement statement2 = DBQuery.getStatement();
            statement2.execute(retrieveStatement);
            ResultSet rs = statement.getResultSet();
   
            while (rs.next()){
                Appointment appointment = new Appointment(
                    rs.getInt("Appointment_ID"), 
                    rs.getString("Title"), 
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getString("Start"),
                    rs.getString("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("Contact_ID"));
            
            // Identify the contact and customer associated with the appointment
            appointment.setupContact();
            appointment.setupCustomer();
            appointments.add(appointment);}
             
        } catch(SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    

    /**
     * Updates an existing appointment.
     * @param a The appointment object that is going to be updated
     */
    public static void updateOldAppointment(Appointment a){
       
        try {
            String updateStatement = String.format("UPDATE appointments SET Title='%s',"
            + " Description='%s', Location='%s', Type='%s', Start='%s', End='%s', Last_Update=NOW(), Last_Updated_By='%s', Customer_ID=%d, User_ID=%d, Contact_ID=%d WHERE Appointment_ID=%d", a.getTitle(), 
            a.getDescription(), a.getLocation(), a.getType(), a.getStart(), a.getEnd(), DBLogin.getMainUser().getUsername(), a.getCustomerId(), DBLogin.getMainUser().getUserId(), a.getContact().getContactId(), a.getId());
            
            Statement statement = DBQuery.getStatement();
            statement.execute(updateStatement);
            
        } catch(SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
  
    /**
     * Used to associate a contact with a particular appointment
     * @param contactId ID of the contact to retrieve from the database
     * @return a contact object
     */
    public static Contact getContact(int contactId) throws SQLException{
        
        Contact contact = new Contact();
        String selectStatement = String.format("SELECT * FROM contacts WHERE Contact_ID=%d", contactId);
        
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        ResultSet rs = statement.getResultSet();
     
        while (rs.next()){
            contact.setContactId(rs.getInt("Contact_ID"));
            contact.setContactName(rs.getString("Contact_Name"));
            contact.setContactEmail(rs.getString("Email"));
        }
        return contact;}
    

    /**
     * Used to associate a Customer with a particular appointment
     * @param customerId ID of the customer to retrieve from the database
     * @return A customer object
     */
    public static Customer getCustomer(int customerId) throws SQLException{
        
        Customer customer = new Customer();
        String selectStatement = String.format("SELECT * FROM customers WHERE Customer_ID=%d", customerId);
        
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        ResultSet rs = statement.getResultSet();
      
        while (rs.next()){
            customer.setName(rs.getString("Customer_Name"));
    }
   
        return customer;}

    
    /**
     * Load all the appointments currently in the database into individual Appointment objects
     * @return An ObservableList of appointment objects
     */
    public static ObservableList<Appointment> loadAppointments() throws SQLException{
        
        String selectStatement = "SELECT * FROM appointments";
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        
        ResultSet rs = statement.getResultSet();
   
        while (rs.next()){
              
                Appointment appointment = new Appointment(
                    rs.getInt("Appointment_ID"), 
                    rs.getString("Title"), 
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getString("Start"),
                    rs.getString("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("Contact_ID"));
               
                appointments.add(appointment);}
        
        // set Contact and Customer for each appointment in appointments list
        for (Appointment appointment : appointments){
            appointment.setupContact();
            appointment.setupCustomer();}
        return appointments;}
    

    /**
     * Delete an appointment from the database
     * @param selectedAppointment The appointment object that will be removed from the database and appointments list
     */
    public static void deleteAppointment(Appointment selectedAppointment){
        // first remove from appointments list to keep everything updated
        appointments.remove(selectedAppointment);
        
        try {
            // create delete statement that locates appointment by ID and execute
            String deleteStatement = String.format("DELETE FROM appointments WHERE Appointment_ID=%d", selectedAppointment.getId());
            Statement statement = DBQuery.getStatement();
            statement.execute(deleteStatement);
        } catch(SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    

    /**
     * Used for retrieving all Contacts from database to prepopulate comboboxes on the NewAppointment and UpdateOldAppointment screens
     * @return An ObservableList of contact objects
     */
    public static ObservableList getContacts() {

        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String selectStatement = "SELECT * FROM contacts";
        try {
            Statement statement = DBQuery.getStatement();
            statement.execute(selectStatement);
        
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                int contactId = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                Contact c = new Contact(contactId, contactName);
                contacts.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contacts;
    } 
}

