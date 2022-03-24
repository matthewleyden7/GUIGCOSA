
package controllers;

import database.DBAppointment;
import database.DBConnect;
import database.DBCustomer;
import database.DBLogin;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import models.Appointment;

/**
 * Controls the operations of the main screen
 * @author Matthew Leyden
 */
public class MainScreenController implements Initializable {
    
    // Set up buttons, labels, and TextArea (used for alerts)
    @FXML private Button customerRcdBtn;
    @FXML private Button appointmentsBtn;
    @FXML private Button reprortsBtn;
    @FXML private Button exitBtn;
    @FXML private Label welcomeLabel;
    @FXML private TextArea alertTextArea;
    @FXML private Label alertLabel;
    
    
    /**
     * Changes to the Customer Records screen
     * @param e ActionEvent used to change screen to CustomerRecords.fxml
     */
    public void customerRcdBtnPressed(ActionEvent e) throws IOException{
           Stage stage = new Stage();
           Parent root = FXMLLoader.load(getClass().getResource("/views/CustomerRecords.fxml"));
           Scene scene = new Scene(root);
           stage.setScene(scene);
           stage.show();
           ((Node) (e.getSource())).getScene().getWindow().hide();}
    
    /**
     * Change to the Reports screen
     * @param e ActionEvent used to change screen to Reports.fxml
     */
    public void reportsBtnPressed(ActionEvent e) throws IOException{
           Stage stage = new Stage();
           Parent root = FXMLLoader.load(getClass().getResource("/views/Reports.fxml"));
           Scene scene = new Scene(root);
           stage.setScene(scene);
           stage.show();
           ((Node) (e.getSource())).getScene().getWindow().hide();}
    
 
    /**
     * Change to the appointments screen
     * @param e ActionEvent used to change to screen Appointments.fxml
     */
    public void appointmentsBtnPressed(ActionEvent e) throws IOException{
           Stage stage = new Stage();
           Parent root = FXMLLoader.load(getClass().getResource("/views/Appointments.fxml"));
           Scene scene = new Scene(root);
           stage.setScene(scene);
           stage.show();
           ((Node) (e.getSource())).getScene().getWindow().hide();}
    

    /**
     * Close database connection and exit
     */
    public void exitBtnPressed(){
        DBConnect.closeConnection();
        System.exit(0);}
    
    /**
     * Initializes the Main Screen
     * <p>Load customers from database with loadCustomers method of the DBCustomer class if first time on main screen.</p>
     * <p>Load appointments from the database with loadAppointments method of the DBAppointment class if first time on main screen</p>
     * <p>Create a LocalDateTime object for 15 minutes from now</p>
     * <p>LAMBDA expression is used here to efficiently and quickly filter the appointments that have both a start time that is before LocalDateTime fifteenMin and after the current LocalDateTime.</p>
     * <p>Set alertLabel and alertTextArea</p>
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        welcomeLabel.setText("Welcome " + DBLogin.getMainUser().getUsername() + "! What would you like to do today?");
        
        if (DBCustomer.customers.isEmpty()){
            try {
                DBCustomer.loadCustomers();
            } catch (SQLException e) {
                System.out.println(e.getMessage());}}
        
        if (DBAppointment.appointments.isEmpty()){
            try {
                DBAppointment.loadAppointments();
            } catch (SQLException e) {
                System.out.println(e.getMessage());}}
        
        // create a LocalDateTime object for 15 minutes from now.
        LocalDateTime fifteenMin = LocalDateTime.now().plusMinutes(15);
        
   
        ObservableList<Appointment> alert = DBAppointment.appointments.filtered(a -> a.getStartDateTime().isBefore(fifteenMin) && a.getStartDateTime().isAfter(LocalDateTime.now()));
        if (!alert.isEmpty()){
            alertLabel.setText("Please be advised. You have an appt within 15 minutes...");
            for (Appointment appointment : alert)
                alertTextArea.setText("Appt ID: " + appointment.getId() + "\nDate: " + appointment.getStartDate() + "\nTime: " + appointment.getStartTime());
        } else {
            alertTextArea.setText("There are no upcoming appointments within 15 minutes.");
        }
    }    
    
}
