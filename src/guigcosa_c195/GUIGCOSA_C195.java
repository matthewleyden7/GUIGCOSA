
package guigcosa_c195;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.DBConnect;
import database.DBQuery;

/**
 * Boots up the program and opens the Login Form Screen
 * @author Matthew Leyden
 */
public class GUIGCOSA_C195 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/views/LoginForm.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Login Form");
        stage.show();
    }
    
    public static void main(String[] args) {
   
        launch(args);
        DBConnect.closeConnection();

    }


}
