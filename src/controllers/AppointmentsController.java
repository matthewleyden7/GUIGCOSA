/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.DBAppointment;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import models.Appointment;
import models.displayAlert;


/**
 * Controls operations of the main appointments screen
 * @author Matthew Leyden
 */
public class AppointmentsController implements Initializable {

    // set up buttons
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn;
    
    // Set up radio buttons.
    @FXML private RadioButton allRadioBtn;
    @FXML private RadioButton weekRadioBtn;
    @FXML private RadioButton monthRadioBtn;
    private ToggleGroup apptToggle;
    
    // set up table
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> idColumn;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> descriptionColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, String> contactColumn;
    @FXML private TableColumn<Appointment, String> customerColumn;
    @FXML private TableColumn<Appointment, String> typeColumn;
    @FXML private TableColumn<Appointment, String> startDateColumn;
    @FXML private TableColumn<Appointment, String> startTimeColumn;
    @FXML private TableColumn<Appointment, String> endDateColumn;
    @FXML private TableColumn<Appointment, String> endTimeColumn;
    @FXML private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML private TableColumn<Appointment, Integer> contactIdColumn;

    // Appointment object selectedAppointment is used for sharing between screens
    public static Appointment selectedAppointment;
    

    /**
     * Change to the New Appointments screen
     * @param e ActionEvent used to change screen to NewAppointment.fxml 
     */
    public void addBtnPressed(ActionEvent e) throws IOException{
       Stage stage = new Stage();
       Parent root = FXMLLoader.load(getClass().getResource("/views/NewAppointment.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       ((Node) (e.getSource())).getScene().getWindow().hide();
    }
    

    /**
     * Change to the Update Appointment screen
     * <p>Verify user chose an appointment to modify and display alert if not</p>
     * <p>Set the chosen appointment to variable selectedAppointment</p>
     * <p>change to UpdateAppointment screen</p>
     * @param e ActionEvent used to change screens to UpdateAppointment.fxml
     */
    public void updateBtnPressed(ActionEvent e) throws IOException{
       if (appointmentTable.getSelectionModel().getSelectedItem() == null){
            new displayAlert().show("Update error", "Update error", "Please choose an appointment to update.");
       } else {
            selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
       
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/UpdateAppointment.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();}}
    

    /**
     * Deletes an appointment
     * <p>Verifies user chose an appointment to delete and displays confirmation alert</p>
     * <p>Sends to DBAppointment for removal</p>
     */
    public void deleteBtnPressed() throws SQLException {
        Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (appointmentTable.getSelectionModel().getSelectedItem() == null){
            new displayAlert().show("Deletion Error", "Invalid choice", "Please choose an appointment to cancel.");
        } else {
            Optional<ButtonType> confirmation = new displayAlert().showOptional("Confirm cancellation", "Confirm Appointment Cancellation", String.format("Are you sure you want to cancel this Appointment?\nAppointment ID:  %d\nAppointment Type:  %s", appointment.getId(), appointment.getType()));
            if (confirmation.get() == ButtonType.OK) {
                DBAppointment.deleteAppointment(appointment);}}}
             

    /**
     * Filter appointments by week
     * <p>LAMBDA expression used to quickly and efficiently filter appointments by whether or not they start this week by comparing them to LocalDateTime week object</p>
     * <p>Place appointments into List weekAppointments and set appointmentTable</p>
     */
    public void weekRadioBtnPressed(){
      
        DBAppointment.weekAppointments.clear();
        LocalDateTime week = LocalDateTime.now().plusWeeks(1);
        DBAppointment.weekAppointments = DBAppointment.appointments.filtered(a -> a.getStartDateTime().isBefore(week));
        appointmentTable.setItems(DBAppointment.weekAppointments);}
    
    
     /**
     * Filter appointments by month
     * <p>LAMBDA expression used to quickly and efficiently filter appointments by whether or not they start this month by comparing them to LocalDateTime month object</p>
     * <p>Place appointments into List monthAppointments and set appointmentTable</p>
     */
    public void monthRadioBtnPressed(){
   
        DBAppointment.monthAppointments.clear();
        LocalDateTime month = LocalDateTime.now().plusMonths(1);
        DBAppointment.monthAppointments = DBAppointment.appointments.filtered(a -> a.getStartDateTime().isBefore(month));
        appointmentTable.setItems(DBAppointment.monthAppointments);}

    

    /**
     * Sets the appointment table with all items in the appointments list of the DBAppointment class.
     */
    public void allRadioBtnPressed(){
       
        appointmentTable.setItems(DBAppointment.appointments);
    }
    

    /**
     * Returns to the Main Screen
     * @param e ActionEvent used to change screens to MainScreen.fxml
     * @throws IOException 
     */
    public void backBtnPressed(ActionEvent e) throws IOException{
       Stage stage = new Stage();
       Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       ((Node) (e.getSource())).getScene().getWindow().hide();}
    
    
    /**
     * Initialize the Appointments screen
     * <P>Initialize all the columns of the appointmentTable</p>
     * <p>Populate the Appointment table</p>
     * <p>Set Radio Button Toggle Group</p> 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
           
            idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
            descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
            typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
            locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
            startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate()));
            startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
            endDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate()));
            endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
            contactColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContact().getContactName()));
            customerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getName()));
            contactIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContact().getContactId()).asObject());
            customerIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
         
        // If appointments list of DBAppointment class is empty, we need to populate it with appointments that
        // are currently in the database and set appointmentTable with them. Otherwise, set appointmentTable
        // with appointments in appointments list (much faster)
        if (DBAppointment.appointments.isEmpty()){
            try {
                System.out.println("loading appointments from database");
                appointmentTable.setItems(DBAppointment.loadAppointments());
           
            } catch (SQLException ex) {
                System.out.println("error --> " + ex.getMessage());
            }
        } else {
            appointmentTable.setItems(DBAppointment.appointments);
        }
        
        apptToggle = new ToggleGroup();
        this.allRadioBtn.setToggleGroup(apptToggle);
        this.weekRadioBtn.setToggleGroup(apptToggle);
        this.monthRadioBtn.setToggleGroup(apptToggle);
        apptToggle.selectToggle(allRadioBtn);
        
    }    
    
}
