
package models;

import controllers.LoginFormController;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


/**
 * Tracker class used to write login attempts information to login_activity.txt
 * @author Matthew Leyden
 */
public class Tracker {
    
    /**
     * Records and timestamps every user Login attempt and whether it was a success or failure
     * @param uName User-entered username
     * @param attempt True if valid False if invalid
     */
    public static void record(String uName, boolean attempt) {
        
        try (FileWriter fwriter = new FileWriter("login_activity.txt", true);
            BufferedWriter bwriter = new BufferedWriter(fwriter);
            PrintWriter pwriter = new PrintWriter(bwriter)) {

            pwriter.println(ZonedDateTime.now() + " : " + uName + " ---> " +  (attempt ? " Success" : " Failure"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public Tracker() {}
}
