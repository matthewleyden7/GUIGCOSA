
package controllers;

import com.mysql.jdbc.Statement;
import database.DBCustomer;
import database.DBQuery;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Country;
import models.State;
import models.displayAlert;
import models.Customer;


/**
 * Controls the operations of the UpdateCustomer screen
 * @author Matthew Leyden
 */
public class UpdateCustomerController implements Initializable {
    
    // set up TextFields
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField zipField;
    
    // comboboxes for countries and states
    @FXML private ComboBox<Country> countryBox;
    @FXML private ComboBox<State> stateBox;
    
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;    
    
    // Customer selectedCustomer is used to share information between screens.
    public static Customer selectedCustomer;
   
    
    /**
     * Filter the first-level-divisions based on user-chosen country
     */
    public void filterStates() {
        System.out.println(countryBox.getValue().getCountryId());
        stateBox.getItems().clear();
        stateBox.getItems().addAll(DBCustomer.getCitiesForCountry(countryBox.getValue().getCountryId()));
        stateBox.setValue(null);}
    
    
    /**
     * Check for errors in user-entered TextFields and ComboBoxes
     * <p>Display alert if error found</p>
     * @return A boolean: If true, errors found and must be fixed, if false, continue to update customer
     */
    public boolean errorHandling(){
        // creates a new displayAlert object
        displayAlert error = new displayAlert();
        if (nameField.getText().equals("")){
            error.show("Name error", "Invalid name", "Please enter a name."); return true;}
        if (addressField.getText().equals("")){
            error.show("Address error", "Invalid address", "Please enter an address for new customer " + nameField.getText()); return true;}
        if (phoneField.getText().equals("")){
            error.show("Phone error", "Invalid phone", "Please enter a phone number for new customer " + nameField.getText()); return true;}
        if (zipField.getText().equals("")){
            error.show("Postal code error", "Invalid postal code", "Please enter a postal code for new customer " + nameField.getText()); return true;}
        if (countryBox.getValue() == null){
            error.show("Country error", "Invalid country", "Please choose a country for new customer " + nameField.getText()); return true;}
        if (stateBox.getValue() == null){
            error.show("State error", "Invalid state/province", "Please choose a state/province for new customer " + nameField.getText()); return true;}
        return false;}
    
    
    /**
     * Update the customer
     * <p>Collect information from TextFields and check for errors</P>
     * <p>Update the customer in the database using the updateOldCustomer method of the DBCustomer class</p>
     * <p>Change back to customer records screen</p>
     * @param e ActionEvent used to change screen to CustomerRecords.fxml
     */
    public void updateBtnPressed(ActionEvent e) throws SQLException, IOException{
        boolean findErrors = errorHandling();
        if (findErrors == false){
            if (!selectedCustomer.getName().equals(nameField.getText()))
                selectedCustomer.setName(nameField.getText());
            if (!selectedCustomer.getAddress().equals(addressField.getText()))
                selectedCustomer.setAddress(addressField.getText());
            if (!selectedCustomer.getPhone().equals(phoneField.getText()))
                selectedCustomer.setPhone(phoneField.getText());
            if (!selectedCustomer.getZip().equals(zipField.getText()))
                selectedCustomer.setZip(zipField.getText());
            if (!selectedCustomer.getState().equals(stateBox.getSelectionModel().getSelectedItem()))
                selectedCustomer.setState(stateBox.getSelectionModel().getSelectedItem());
                selectedCustomer.setDivisionId(stateBox.getSelectionModel().getSelectedItem().getDivisionId());
            
            DBCustomer.updateOldCustomer(selectedCustomer);
            
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/CustomerRecords.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();}}
    
    
    /**
     * Return to Customer Records screen w/o saving changes
     * @param e ActionEvent used to change screens to CustomerRecords.fxml
     */
    public void cancelBtnPressed(ActionEvent e) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/CustomerRecords.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        ((Node) (e.getSource())).getScene().getWindow().hide();}

    /**
     * Pre-set all the TextFields, ComboBoxes, and DatePickers with selected customers information
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // // Grab the selected customer from the previous customer screen.
        selectedCustomer = CustomerRecordsController.selectedCustomer;
        
        // prepopulate the fields and comboboxes with selected customers values.
        idField.setText(Integer.toString(selectedCustomer.getId()));
        idField.setDisable(true);
        nameField.setText(selectedCustomer.getName());
        addressField.setText(selectedCustomer.getAddress());
        phoneField.setText(selectedCustomer.getPhone());
        zipField.setText(selectedCustomer.getZip());
        countryBox.getItems().addAll(DBCustomer.getCountries());
        this.countryBox.setValue(selectedCustomer.getCountry());
        filterStates();
        this.stateBox.setValue(selectedCustomer.getState());
        
        
       
   
    }    
}



