
package database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.SQLException;

/**
 * Handles providing Statement object to execute SQL statement strings
 * @author Matthew Leyden
 */
public class DBQuery {
    
    private static Statement statement; // statement reference
    
    /**
     * Creates a statement object
     * @param conn database connection object
     */
    public static void setStatement(Connection conn) throws SQLException{statement = (Statement) conn.createStatement();}
    

    /**
     * Returns statement object to requested class/controller
     * @return Statement object
     */
    public static Statement getStatement(){return statement;}
    
    // create a prepared statement object
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException{statement = (Statement) conn.prepareStatement(sqlStatement);}
    
    public static PreparedStatement getPreparedStatement(){return (PreparedStatement) statement;}
}
