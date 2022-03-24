
package database;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.SQLException;

import models.User;
import models.displayAlert;
import java.sql.ResultSet;
import models.Tracker;
import database.DBQuery;

/**
 * Handles Logging in to the program
 * @author Matthew Leyden
 */
public class DBLogin {
    
    private static User mainUser = new User();
    

    /**
     * Attempts to Log User in
     * <p>Displays alert if username invalid or password invalid</p>
     * <p>Sends login attempt to Tracker class to record login activity</p>
     * @param uName User-entered username
     * @param pWord User-entered password
     * @return A boolean: If true, user entered a valid username and password, if false, user entered an invalid username or password 
     */
    public static Boolean login(String uName, String pWord) throws SQLException {
   
            if (uName.equals("")) {
                new displayAlert().show("Error", "Invalid username", "Please enter a username");
                return false;
            } else if (pWord.equals("")) {
                new displayAlert().show("Error", "Invalid password", "Please enter a password");
                return false;
            } else {
                String selectStatement = "SELECT * FROM users";
                Connection conn = DBConnect.startConnection();
                DBQuery.setStatement(conn);
                Statement statement = DBQuery.getStatement();
                statement.execute(selectStatement);

                ResultSet rs = statement.getResultSet();
                while (rs.next()){
                    if (rs.getString("User_Name").equals(uName) && rs.getString("Password").equals(pWord)){
                        mainUser.setUsername(rs.getString("User_Name"));
                        mainUser.setUserId(rs.getInt("User_ID"));
                        
                        new Tracker().record(uName, true);
                        return true;
                    }
                }}
            new Tracker().record(uName, false);
            return false;
    }
    

    /**
     * Username and ID information can be retrieved for future reference
     * @return Current User object
     */
    public static User getMainUser() {
        return mainUser;
    }
    
}