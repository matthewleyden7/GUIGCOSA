
package controllers;


import database.DBCustomer;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
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


/**
 * Adds a new customer to the database
 * @author Matthew Leyden
 */
public class AddNewCustomerController implements Initializable {
    
    // setup textfields
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField zipField;
    
    // set up comboboxes for state and country
    @FXML private ComboBox<Country> countryBox;
    @FXML private ComboBox<State> stateBox;
    
    // set up buttons
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;    
    
    // used to filter the first-level-divisions based on user-chosen country.
    /**
     * used to filter the first-level-divisions based on user-chosen country.
     */
    public void filterStates() {
        System.out.println(countryBox.getValue().getCountryId());
        stateBox.getItems().clear();
        stateBox.getItems().addAll(DBCustomer.getCitiesForCountry(countryBox.getValue().getCountryId()));
        stateBox.setValue(null);}
    
    // checking for errors before submission
    /**
     * Check for errors in user-entered TextFields and ComboBoxes
     * <p>Display alert if error found</p>
     * @return A boolean If true, there were errors that the user will need to correct before adding the new customer
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
     * Add the new customer
     * <p>Collect information from TextFields and check for errors</P>
     * <p>Add the customer to the database using the addNewCustomer method of the DBCustomer class</p>
     * <p>Change back to customer records screen</p>
     * @param e ActionEvent used to change screen to CustomerRecords.fxml
     */
    public void addBtnPressed(ActionEvent e) throws SQLException, IOException{
        boolean findErrors = errorHandling();
        if (findErrors == false){
            String name = nameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String zip = zipField.getText();
            String country = countryBox.getValue().toString();
            int divisionId = stateBox.getValue().getDivisionId();
            
            DBCustomer.saveNewCustomer(name, address, phone, zip, divisionId);
            
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/CustomerRecords.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();}}
    

    /**
     * Return to the Customer Records screen w/o saving changes
     * @param e ActionEvent used to change screens to CustomerRecords.fxml
     */
    public void cancelBtnPressed(ActionEvent e) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/customerRecords.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        ((Node) (e.getSource())).getScene().getWindow().hide();}

    
    /**
     * Initialize the countries ComboBox and disable the ID TextField
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // disable id text field.
        idField.setText("auto generate");
        idField.setDisable(true);
        
        // initialize choices for country combo box.
        countryBox.getItems().addAll(DBCustomer.getCountries());
    }    
    
}
