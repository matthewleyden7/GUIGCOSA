
package controllers;

import database.DBAppointment;
import database.DBCustomer;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Appointment;
import models.Contact;
import models.Customer;
import models.convert;
import models.displayAlert;

/**
 * Controls the operations of the NewAppointment screen
 * @author Matthew Leyden
 */
public class NewAppointmentController implements Initializable {

    // buttons
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;

    // setup a Contact combobox and a Customer combobox.
    @FXML private ComboBox<Contact> contactBox;
    @FXML private ComboBox<Customer> customerBox;
    
    // set up TextFields
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField apptTypeField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField apptIdField;

    @FXML private DatePicker startDateField;
    @FXML private DatePicker endDateField;
    
    // startUTC and endUTC strings used to capture converted user entered local time to UTC time for database.
    public String timeString;
    public String startUTC;
    public String endUTC;
    
    // Grab the localZoneID and localZonedDT from LoginFormController for time conversions
    public ZoneId localZoneID = LoginFormController.localZoneId;
    public ZonedDateTime localZonedDT = LoginFormController.localZonedDT;
    
    // Grab the businessZoneID and businessZonedDT from LoginFormController for time conversions
    public ZoneId businessZoneId = LoginFormController.businessZoneId;
    public ZonedDateTime businessZonedDT = LoginFormController.businessZonedDT;
    
    private int businessOpen = 8;
    private int businessClose = 22;
    
    /**
     * prepopulates the DatePicker endDate to the user entered start date
     */
    public void setEndDate(){
        this.endDateField.setValue(startDateField.getValue());}

     /**
     * Checks for errors in all user-entered values
     * <p>Perform time conversions to 24 hour format, UTC, and EST</p>
     * <p>Verify time and date entered are in the future, occur during business hours, and don't conflict with other appointments</p>
     * @return A boolean: If true, errors were found and must be fixed, if false, continue to add new appointment
     */
    public boolean errorHandling() throws ParseException{
        displayAlert error = new displayAlert();
     
        if (customerBox.getValue() == null){
            error.show("Name error", "Invalid name", "Please choose a customer."); return true;}
        if (contactBox.getValue() == null){
            error.show("State error", "Invalid state/province", "Please choose a contact for this appointment "); return true;}
        if (titleField.getText().equals("")){
            error.show("Title error", "Invalid Title", "Please enter a title for this appointment"); return true;}
        if (descriptionField.getText().equals("")){
            error.show("Description error", "Invalid Description", "Please enter a short description for this appointment"); return true;}
        if (locationField.getText().equals("")){
            error.show("Location error", "Invalid location", "Please enter a location for this appointment"); return true;}
        if (apptTypeField.getText().equals("")){
            error.show("Type error", "Invalid Type", "Please enter the type of appointment."); return true;}
        if (startDateField.getValue() == null){
            error.show("Date error", "Invalid Date", "Please choose a start date for this appointment."); return true;}
        if (endDateField.getValue() == null){
            error.show("Date error", "Invalid Date", "Please choose an end date for this appointment."); return true;}
        if (startTimeField.getText().equals("")){
            error.show("Time error", "Invalid Time", "What time is the appointment?"); return true;}
        if (endTimeField.getText().equals("")){
            error.show("Time error", "Invalid Time", "What time is the appointment?"); return true;}
        
        // convert user entered time to 24 hour format. Uses the to24Hour method of the
        // convert class. Parses user entered time and converts to appropriate format.
        String clock24HourStart = new convert().to24Hour(startTimeField.getText());
        String clock24HourEnd = new convert().to24Hour(endTimeField.getText());

        // convert start and end time strings to LocalTime, convert date to LocalDate
        LocalTime startTime = LocalTime.parse(clock24HourStart);
        LocalDate startDate = startDateField.getValue();
        LocalTime endTime = LocalTime.parse(clock24HourEnd);
        LocalDate endDate = endDateField.getValue();
        
        // convert both start and end times to UTC format for database
        ZonedDateTime startZDT = ZonedDateTime.of(startDate, startTime, this.localZoneID);
        this.startUTC = ZonedDateTime.parse(startZDT.toInstant().toString()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ZonedDateTime endZDT = ZonedDateTime.of(endDate, endTime, this.localZoneID);
        this.endUTC = ZonedDateTime.parse(endZDT.toInstant().toString()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // convert both start and end times to EST ZOnedDateTimes
        ZonedDateTime startEST = startZDT.withZoneSameInstant(this.businessZoneId);
        ZonedDateTime endEST = endZDT.withZoneSameInstant(this.businessZoneId);
   
        // bounds checking for start and end times. 1. start time not greater than business close,
        // 2. end time not greater than business close, 3. end time is not less than start time.
        // 4. start date not before todays date. 5. If date today, start time not before now.
        if ((startEST.getHour() == businessClose && startEST.getMinute() > 0) || (startEST.getHour() > businessClose) || (startEST.getHour() < businessOpen)){
            error.show("Time Error", "Invalid Time", "The business would be closed before the start time. Please enter another time. Business hours are 8 am to 10 pm EST."); return true;}
        if ((endEST.getHour() == businessClose && endEST.getMinute() > 0) || (endEST.getHour() > businessClose) || (endEST.getHour() < businessOpen)){
            error.show("Time Error", "Invalid Time", "The business would be closed before the End time. Please enter another time. Business hours are 8 am to 10 pm EST."); return true;}
        if (endTime.isBefore(startTime) && (startDate.isBefore(endDate) || startDate.isEqual(endDate))) {
            error.show("Time Error", "Invalid Time", "Please enter an appointment end time that is after the start time."); return true;}
        if (endDate.isBefore(startDate)) {
            error.show("Date Error", "Invalid Date", "The end date cannot be before the start date."); return true;}
        if (startDate.isBefore(LocalDate.now())){
            error.show("Date Error", "Invalid Date", "Please choose a future date for this appointment."); return true;}
        if (startDate.isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now())){
            error.show("Time Error", "Invalid Time", "This time has already passed. Please enter a future time."); return true;}
        
        // Make sure new appointment time does not conflict with existing appointment time for customer.
        for (Appointment appointment : DBAppointment.appointments){
            if (appointment.getCustomerId() == customerBox.getValue().getId()){
       
                if ((startZDT.isBefore(appointment.getLocalEndZDT()) && (startZDT.isAfter(appointment.getLocalStartZDT()) || startZDT.isEqual(appointment.getLocalStartZDT())))
                || ((endZDT.isBefore(appointment.getLocalEndZDT()) || endZDT.isEqual(appointment.getLocalEndZDT())) && (endZDT.isAfter(appointment.getLocalStartZDT()) || endZDT.isEqual(appointment.getLocalStartZDT())))
                || startZDT.isBefore(appointment.getLocalStartZDT()) && endZDT.isAfter(appointment.getLocalEndZDT())){
                    
                    error.show("Appointment Error", "Time Conflict", "Customer " + appointment.getCustomerName() + " already has an appointment on " + 
                    appointment.getStartDate() + " with " + appointment.getContactName() + " from " + appointment.getStartTime() + " to " 
                    + appointment.getEndTime() + ". Please choose another time."); return true;}}}
            

        return false;}
    

    /**
     * Add a new appointment
     * <p>Collect information from TextFields and check for errors</P>
     * <p>Save a new appointment to the database using the saveNewAppointment method of the DBAppointment class</p>
     * <p>Change back to appointments screen</p>
     * @param e ActionEvent used to change screen to Appointments.fxml
     */
    public void addBtnPressed(ActionEvent e) throws SQLException, IOException, ParseException{
        boolean findErrors = errorHandling();
        if (findErrors == false){
            String title = titleField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
            String type = apptTypeField.getText();
            int customerId = customerBox.getValue().getId();
            int contactId = contactBox.getValue().getContactId();
            
            DBAppointment.saveNewAppointment(title, description, location, type, this.startUTC, this.endUTC, customerId, contactId);
           
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/appointments.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();}}
    

    /**
     * Change back to the appointments screen w/o saving changes
     * @param e ActionEvent used to change to screen Appointments.fxml
     */
    public void cancelBtnPressed(ActionEvent e) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/Appointments.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        ((Node) (e.getSource())).getScene().getWindow().hide();}
    
    
    /**
     * Initialize the New Appointment Screen
     * <p>Set Contact ComboBox with available contacts and Customer ComboBox with available customers. </p>
     * <p>Disable Appointment ID TextField</p>
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        contactBox.getItems().addAll(DBAppointment.getContacts());
        customerBox.getItems().addAll(DBCustomer.customers);
        
        apptIdField.setText("auto generate");
        apptIdField.setDisable(true);
        
   
         
    }    
    
}
