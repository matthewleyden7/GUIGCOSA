package database;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles connection to the database
 * @author Matthew Leyden
 */
public class DBConnect {

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/WJ05VRp";
    
    // JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    
    // Driver interface and connection reference
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    private static final String username = "U05VRp";
    private static String password = "53688617769";
    private static Connection conn;
    
   
    /**
     * Starts the connection to the database
     * @return Connection object
     */
    public static Connection startConnection(){
       
        try{
            Class.forName(MYSQLJDBCDriver);
            conn = (Connection)DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("connection successful!");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("sorry, class not found exception");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection(){
        try{
            conn.close();
            System.out.println("Connection closed");
        } catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
