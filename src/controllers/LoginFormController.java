
package controllers;

import database.DBAppointment;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import database.DBLogin;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javafx.scene.control.Label;
import models.displayAlert;


/**
 * Controls the operations of the Login screen
 * @author Matthew Leyden
 */
public class LoginFormController implements Initializable {

    // set up textfields and button
    @FXML private TextField userName;
    @FXML private PasswordField password;
    @FXML private Button loginBtn;
    
    // set up labels
    @FXML private Label uNameLabel;
    @FXML private Label pWordLabel;
    @FXML private Label zoneLabel;
    @FXML private Label businessTimeLabel;
    @FXML private Label localTimeLabel;
    @FXML private Label loginLabel;
    
    // 
    private ResourceBundle rb;

    // setup zone id and ZonedDateTime objects for local and business
    public static ZoneId localZoneId;
    public static ZonedDateTime localZonedDT;
    public static ZoneId businessZoneId;
    public static ZonedDateTime businessZonedDT;
    

    /**
     * Attempt to Login the User
     * <p>Get username and password from TextFields</p>
     * <p>verify that user entered accurate information with the Login method of the DBLogin class</p>
     * <p>If valid, change to Main Screen, else display Alert</p>
     * @param e ActionEvent that changes screen to MainScreen.fxml
     */
    public void loginBtnPressed(ActionEvent e) throws IOException, SQLException{
        
        String uName = userName.getText();
        String pWord = password.getText();
        boolean valid = DBLogin.login(uName, pWord);
        if(valid) {
           
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/mainScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            ((Node) (e.getSource())).getScene().getWindow().hide();
        } else {
           
            new displayAlert().show(rb.getString("Error"), rb.getString("Invalid"), rb.getString("ErrorMessage"));}
        }
    
    /**
     * Initialize the Login Form
     * <p>Set the resource bundle to the language of the users default locale and set all labels</p>
     * <p>Get a Zone ID and ZonedDateTime object for user</p> 
     * <p>Get a Zone ID and ZonedDateTime object for business</p>
     * <p>Set Labels zoneLabel, localTimeLabel, and businessTimeLabel with location, local time, and business time respectively</p>
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Set the resource bundle to the language of users default Locale.
        rb = ResourceBundle.getBundle("languages/Nat", Locale.getDefault());
        this.rb = rb;
 
        uNameLabel.setText(rb.getString("Username"));
        pWordLabel.setText(rb.getString("Password"));
        loginBtn.setText(rb.getString("Login"));
        loginLabel.setText(rb.getString("Login"));
        
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        this.localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        this.localZonedDT = ZonedDateTime.of(date, time, this.localZoneId);
        
        Date date3 = new Date();  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("EST")); 
        LocalDateTime businessDateTime = LocalDateTime.parse(formatter.format(date3));
        this.businessZoneId = ZoneId.of("America/New_York");
        this.businessZonedDT = ZonedDateTime.of(businessDateTime, businessZoneId);
 
        String zone = this.localZoneId.toString();
        String city = zone.substring(zone.indexOf("/") + 1, zone.length());
        String country = zone.substring(0, zone.indexOf("/"));
        zoneLabel.setText(rb.getString("Userconnect") + " " + country + ", " + city + " ---");

        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
        formatter2.setTimeZone(TimeZone.getTimeZone(ZoneId.of(TimeZone.getDefault().getID()))); 
        this.localTimeLabel.setText(rb.getString("Currentlt") + " " + formatter2.format(new Date()) + ".");
        formatter2.setTimeZone(TimeZone.getTimeZone("EST")); 
        this.businessTimeLabel.setText(rb.getString("Currentbt") + " " + formatter2.format(new Date()) + ".");

      
        
        
        
       
    }    
    
}
