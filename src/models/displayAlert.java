
package models;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


/**
 * Class designed to minimize coding - Generates either ERROR alerts or CONFIRMATION alerts
 * @author Matthew Leyden
 */
public class displayAlert {
    String title, header, content;
    
    // no argument constructor
    public displayAlert(){};
    
    /**
     * Generates a Confirmation Alert
     * @param title Title for Alert
     * @param header Header for Alert
     * @param content Content of Alert
     * @return Confirmation of whether user clicked OK or Cancel
     */
    public Optional<ButtonType> showOptional(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setX(650.0);
        alert.setY(515.0);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> confirmation = alert.showAndWait();
        return confirmation;
    }
    
    /**
     * Generates an Error Alert
     * @param title Title for Alert
     * @param header Header for Alert
     * @param content Content of Alert
     */
    public void show(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //alert.setHeight(500.0);
        alert.setX(650.0);
        alert.setY(515.0);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    
}
