
package controllers;

import com.mysql.jdbc.Statement;
import database.DBAppointment;
import database.DBCustomer;
import database.DBQuery;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Appointment;
import models.Customer;

// Controls the operations of the Reports screen.
/**
 * Controls the operations of the Reports screen
 * @author Matthew Leyden
 */
public class ReportsController implements Initializable {

    // Radio buttons
    @FXML private RadioButton apptTypeRadioBtn;
    @FXML private RadioButton contactSchedulesRadioBtn;
    @FXML private RadioButton customerApptRadioBtn;
    private ToggleGroup reportsToggle;
    
    @FXML private Button backBtn;
    
    // 6 TextAreas are used to display information, some may not be used.
    @FXML private TextArea textArea1;
    @FXML private TextArea textArea2;
    @FXML private TextArea textArea3;
    @FXML private TextArea textArea4;
    @FXML private TextArea textArea5;
    @FXML private TextArea textArea6;
    
    // 6 Labels, 1 above each TextArea.
    @FXML private Label label1;
    @FXML private Label label2;
    @FXML private Label label3;
    @FXML private Label label4;
    @FXML private Label label5;
    @FXML private Label label6;

    /**
     * Load Report: Appointment types by month
     * <p>Reset labels to Month, Appointment Type, and Total.</p>
     * <p>Query database and extract information into strings</p>
     * <p>Set TextAreas with appropriate information string.</p>
     */
    public void apptBtnPressed() throws SQLException{
        label1.setText(""); label2.setText(""); label3.setText(""); label4.setText(""); label5.setText(""); label6.setText("");
        textArea1.setText(""); textArea2.setText(""); textArea3.setText(""); textArea4.setText(""); textArea5.setText(""); textArea6.setText("");
        label1.setText("Month");
        label2.setText("Appointment type");
        label3.setText("Total");
        String selectStatement = "SELECT Type, MONTHNAME(Start) as 'Month', COUNT(*) as 'Total' FROM appointments GROUP BY Type, Month";
            
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        ResultSet rs = statement.getResultSet();
        String monthString = "";
        String typeString = "";
        String totalString = "";
        while (rs.next()){
            monthString += rs.getString("Month") + "\n";
            typeString += rs.getString("Type") + "\n";
            totalString += rs.getString("total") + "\n";
        }
        

        textArea1.setText(monthString);
        textArea2.setText(typeString);
        textArea3.setText(totalString);}
    

    /**
     * Load Report: Contact Schedules
     * <p>reset labels to Contact, Appointment ID/Type, Description, Customer ID/Name, Start, and End</p>
     * <p>Extract relevant information from appointments list into ArrayList</p>
     * <p>Add ArrayList to HashMap - key = Contact, value = List of Lists</p>
     * <p>Extract information from HashMap into Strings</p>
     * <p>Set TextAreas with generated information strings</p>
     */
    public void contactBtnPressed() throws SQLException{
        label1.setText(""); label2.setText(""); label3.setText(""); label4.setText(""); label5.setText(""); label6.setText("");
        textArea1.setText(""); textArea2.setText(""); textArea3.setText(""); textArea4.setText(""); textArea5.setText(""); textArea6.setText("");
        label1.setText("Contact");
        label2.setText("Appt ID/Type");
        label3.setText("Description");
        label4.setText("Customer ID/Name");
        label5.setText("Start");
        label6.setText("End");
        String contactString = "";
        String apptString = "";
        String descString = "";
        String customerString = "";
        String startString = "";
        String endString = "";
     
        ArrayList<String> c = new ArrayList<>();
        Map<String, List<List>> map = new HashMap<>();
        for (Appointment appointment : DBAppointment.appointments){
            if (!map.containsKey(appointment.getContactName()))
                map.put(appointment.getContactName(), new ArrayList<List>());
            c.add(appointment.getId() + " / " + appointment.getType());
            c.add(appointment.getDescription());
            c.add(appointment.getCustomerId() + " / " + appointment.getCustomerName());
            c.add(appointment.getStartTime() + " (" + appointment.getStartDate().substring(5, 10) + "-" + appointment.getStartDate().substring(2, 4) + ")");
            c.add(appointment.getEndTime() + " (" + appointment.getEndDate().substring(5, 10) + "-" + appointment.getEndDate().substring(2, 4) + ")");
            map.get(appointment.getContactName()).add((List) c.clone());
            c.clear();}

        // extract information from HashMap into information strings
        for (String key : map.keySet()){
            contactString += key;
            for (List value : map.get(key)){
                contactString += "\n";
                apptString += value.get(0) + "\n";
                descString += value.get(1) + "\n";
                customerString += value.get(2) + "\n";
                startString += value.get(3) + "\n";
                endString += value.get(4) + "\n";}
            contactString += "\n"; apptString += "\n"; descString += "\n"; customerString += "\n"; startString += "\n"; endString += "\n";
        }
        
        textArea1.setText(contactString);
        textArea2.setText(apptString);
        textArea3.setText(descString);
        textArea4.setText(customerString);
        textArea5.setText(startString);
        textArea6.setText(endString);}

    /**
     * Load Report: Customer appointments
     * <p>Reset labels to Customer name, State/Province, Total # appt's</p>
     * <p>Query the database and extract information into strings</p>
     * <p>Set TextAreas with generated information strings</p>
     */
    public void customerApptBtnPressed() throws SQLException{
        label1.setText(""); label2.setText(""); label3.setText(""); label4.setText(""); label5.setText(""); label6.setText("");
        textArea1.setText(""); textArea2.setText(""); textArea3.setText(""); textArea4.setText(""); textArea5.setText(""); textArea6.setText("");
        label1.setText("Customer name");
        label2.setText("State/Province");
        label3.setText("Total # appt's");
        
        String selectStatement = "SELECT customers.Customer_Name, first_level_divisions.Division,  COUNT(*) as 'Total' FROM customers JOIN appointments "
                + "ON customers.Customer_ID = appointments.Customer_ID JOIN first_level_divisions ON first_level_divisions.Division_ID = customers.Division_ID GROUP BY Customer_Name, Division;";
            
        Statement statement = DBQuery.getStatement();
        statement.execute(selectStatement);
        ResultSet rs = statement.getResultSet();
        String custString = "";
        String divisionString = "";
        String numString = "";
   
        while (rs.next()){
            custString += rs.getString("Customer_Name") + "\n";
            divisionString += rs.getString("Division") + "\n";
            numString += rs.getString("Total") + "\n";}
   
        textArea1.setText(custString);
        textArea2.setText(divisionString);
        textArea3.setText(numString);}
    
    /**
     * Change back to the main screen
     * @param e ActionEvent used to change screen to MainScreen.fxml
     */ 
    public void backBtnPressed(ActionEvent e) throws IOException{
       Stage stage = new Stage();
       Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       ((Node) (e.getSource())).getScene().getWindow().hide();}
    
    /**
     * Initialize the radio button toggle and Load Report: Appointment types by month
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Initialize radio button toggle.
        reportsToggle = new ToggleGroup();
        this.apptTypeRadioBtn.setToggleGroup(reportsToggle);
        this.contactSchedulesRadioBtn.setToggleGroup(reportsToggle);
        this.customerApptRadioBtn.setToggleGroup(reportsToggle);
        reportsToggle.selectToggle(apptTypeRadioBtn);
        
        // Load appointment type by month report.
        try {
            apptBtnPressed();
        } catch (SQLException e) {
            System.out.println(e.getMessage());}
    }    
}
