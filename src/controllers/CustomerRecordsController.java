
package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.Customer;
import database.DBCustomer;
import javafx.scene.Node;
import models.displayAlert;


/**
 * Controls the operations of the customer records screen
 * @author Matthew Leyden
 */
public class CustomerRecordsController implements Initializable {
    
    // set up the Customer table
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> stateColumn;
    
    // set up buttons
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn;
    
    // Customer object selectedCustomer is used for sharing information between screens.
    public static Customer selectedCustomer;
    
    // If first visit to customer records screen, we load the customers from the 
    // database. If not, we load from customers List of DBCustomer class for fast insertion
    public static boolean loadCustomers = true;
    
    
    /**
     * Delete a customer
     * <p>Verify user chose customer to delete and display confirmation alert</p>
     * <p>Send customer to DBCustomer for removal</p>
     */
    public void deleteBtnPressed() throws SQLException {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customerTable.getSelectionModel().getSelectedItem() == null){
            new displayAlert().show("Deletion Error", "Invalid choice", "Please choose a customer to delete from the database.");
        } else {
            Optional<ButtonType> confirmation = new displayAlert().showOptional("Confirmation", "Customer Deletion Confirmation", "Are you sure you want to delete " + customer.getName() + " and any future appointments they have?");
            if (confirmation.get() == ButtonType.OK) {
               DBCustomer.deleteCustomer(customer);}}}

    
    /**
     * Change to the Add New Customer Screen
     * @param e ActionEvent used to change screen to AddNewCustomer.fxml
     */
    public void addBtnPressed(ActionEvent e) throws IOException{
       Stage stage = new Stage();
       Parent root = FXMLLoader.load(getClass().getResource("/views/AddNewCustomer.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       ((Node) (e.getSource())).getScene().getWindow().hide();}
    
   
    /**
     * Return to the Main Screen
     * @param e ActionEvent that 
     */
    public void backBtnPressed(ActionEvent e) throws IOException{
       Stage stage = new Stage();
       Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       ((Node) (e.getSource())).getScene().getWindow().hide();}
    
    // 
    /**
     * Change to Update Customer screen
     * <p>Verify user chose customer to update and set chosen customer to selectedCustomer</p> 
     * <p>Change to the UpdateCustomer screen</p>
     * @param e ActionEvent used to change screen to UpdateCustomer.fxml
     */
    public void updateBtnPressed(ActionEvent e) throws IOException{
       if (customerTable.getSelectionModel().getSelectedItem() == null){
            new displayAlert().show("Update error", "Update error", "Please choose a customer to update.");
       } else {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/UpdateCustomer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();}}
    
    /**
     * Initialize the Customer Table and load customers from the database
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Initialize the customer table.
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        stateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStateName()));
        
        
        // Load customers from database with loadCustomers method of the DBCustomer class.
        if (DBCustomer.customers.isEmpty()){
        try {
            customerTable.setItems(DBCustomer.loadCustomers());
        } catch (SQLException e) {
            System.out.println(e.getMessage());}
        } else {
            customerTable.setItems(DBCustomer.customers);
        }
    }    

    
}
