
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
 * Controls the operations of the UpdateAppointment screen
 * @author Matthew Leyden
 */
public class UpdateAppointmentController implements Initializable {
    // buttons
    @FXML private Button cancelBtn;
    @FXML private Button addBtn;
    
    // Setup comboboxes
    @FXML private ComboBox<Contact> contactBox;
    @FXML private ComboBox<Customer> customerBox;
    
    // set up TextFields
    @FXML private TextField apptIdField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField apptTypeField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

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
    
    // public Appointment selectedAppointment is used to share information between screens
    public static Appointment selectedAppointment;
    
    /**
     * prepopulates the DatePicker endDate to the user entered start date, can still change later.
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
            error.show("Name error", "Invalid name", "Please choose a customer name."); return true;}
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
            error.show("Time error", "Invalid Time", "What time does the appointment end?"); return true;}

        // If user modified the start time or end time or start date or end date, do appropriate time conversions and checks.
        if (!selectedAppointment.getEndDate().equals(endDateField.getValue().toString()) || !selectedAppointment.getStartDate().equals(startDateField.getValue().toString())
            || !selectedAppointment.getStartTime().equals(startTimeField.getText()) || !selectedAppointment.getEndTime().equals(endTimeField.getText())){
            
            // convert user entered time to 24 hour format
            String clock24HourStart = new convert().to24Hour(startTimeField.getText());
            String clock24HourEnd = new convert().to24Hour(endTimeField.getText());
            selectedAppointment.setStartTime(clock24HourStart);
            selectedAppointment.setEndTime(clock24HourEnd);
            
            // convert start and end time strings to LocalTime, convert date to LocalDate
            LocalTime startTime = LocalTime.parse(clock24HourStart);
            LocalDate startDate = startDateField.getValue();
            LocalTime endTime = LocalTime.parse(clock24HourEnd);
            LocalDate endDate = endDateField.getValue();

            // convert both start and end times to UTC format for db
            ZonedDateTime startZDT = ZonedDateTime.of(startDate, startTime, this.localZoneID);
            startUTC = ZonedDateTime.parse(startZDT.toInstant().toString()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            selectedAppointment.setStart(startUTC);
        
            ZonedDateTime endZDT = ZonedDateTime.of(endDate, endTime, this.localZoneID);
            endUTC = ZonedDateTime.parse(endZDT.toInstant().toString()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            selectedAppointment.setEnd(endUTC);

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
                        appointment.getStartDate() + " with " + contactBox.getValue().getContactName() + " from " + appointment.getStartTime() + " to " 
                        + appointment.getEndTime() + ". Please choose another time."); return true;}}}}

        return false;}
    
    
     /**
     * Update the selected appointment
     * <p>Collect information from TextFields and check for errors</P>
     * <p>Update the appointment in the database using the updateOldAppointment method of the DBAppointment class</p>
     * <p>Change back to appointments screen</p>
     * @param e ActionEvent used to change screen to Appointments.fxml
     */
    public void addBtnPressed(ActionEvent e) throws SQLException, IOException, ParseException{
        boolean findErrors = errorHandling();
        if (findErrors == false){
            
            // if any textfields have different value from selectedAppointment's value, update with new value.
            if (selectedAppointment.getCustomer() != customerBox.getValue())
                selectedAppointment.setCustomer(customerBox.getValue());
            if (!selectedAppointment.getTitle().equals(titleField.getText()))
                selectedAppointment.setTitle(titleField.getText());
            if (!selectedAppointment.getDescription().equals(descriptionField.getText()))
                selectedAppointment.setDescription(descriptionField.getText());
            if (!selectedAppointment.getLocation().equals(locationField.getText()))
                selectedAppointment.setLocation(locationField.getText());
            if (!selectedAppointment.getType().equals(apptTypeField.getText()))
                selectedAppointment.setType(apptTypeField.getText());
            if (selectedAppointment.getContact() != contactBox.getValue())
                selectedAppointment.setContact(contactBox.getValue());
            if (!selectedAppointment.getStartDate().equals(startDateField.getValue().toString()))
                selectedAppointment.setStartDate(startDateField.getValue().toString());
            if (!selectedAppointment.getEndDate().equals(endDateField.getValue().toString()))
                selectedAppointment.setEndDate(endDateField.getValue().toString());
 

            DBAppointment.updateOldAppointment(selectedAppointment);
   
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/Appointments.fxml"));
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
     * Pre-set all the TextFields, ComboBoxes, and DatePickers with selected appointments information
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Grab the selected appointment from the previous appointments screen.
        selectedAppointment = AppointmentsController.selectedAppointment;
        
        // prepopulate the fields and comboboxes with selected appointments values.
        apptIdField.setText(Integer.toString(selectedAppointment.getId()));
        apptIdField.setDisable(true);
        customerBox.setValue(selectedAppointment.getCustomer());
        contactBox.setValue(selectedAppointment.getContact());
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        apptTypeField.setText(selectedAppointment.getType());
        startTimeField.setText(selectedAppointment.getStartTime());
        endTimeField.setText(selectedAppointment.getEndTime());
        startDateField.setValue(LocalDate.parse(selectedAppointment.getStartDate()));
        endDateField.setValue(LocalDate.parse(selectedAppointment.getEndDate()));
        contactBox.getItems().addAll(DBAppointment.getContacts());
        customerBox.getItems().addAll(DBCustomer.customers);
  
       
   
        
    }    
    
}

