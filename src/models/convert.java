
package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// convert class is used only to parse user entered times and convert them to 24 hour
// format. Should be able to parse a timestring of any type.
public class convert{
    
    public boolean checkForAMPM(String time) {
        if (time.contains("am") || time.contains("pm") || time.contains("AM") || time.contains("PM")){
            return true;
        } else {
            return false;
        }
    }
    public String AMorPM(String time){
        if (time.contains("am"))
            return "am";
        if (time.contains("pm"))
            return "pm";
        if (time.contains("AM"))
            return "AM";
        if (time.contains("PM"))
            return "PM";
        return "not found";
    }
    
    // uses many different types of SimpleDateFormats in effort to extract user entered
    // time and convert to 24 hour format.
    public String to24Hour(String timeString) throws ParseException{
        
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat parseFormat2 = new SimpleDateFormat("ha");
        SimpleDateFormat parseFormat3 = new SimpleDateFormat("hh:mma");
        SimpleDateFormat parseFormat4 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat5 = new SimpleDateFormat("hmm a");
        SimpleDateFormat parseFormat6 = new SimpleDateFormat("hhmm a");
        SimpleDateFormat parseFormat7 = new SimpleDateFormat("hmma");
        SimpleDateFormat parseFormat8 = new SimpleDateFormat("hhmma");
        SimpleDateFormat parseFormat9 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat10 = new SimpleDateFormat("h a");
        Date time;
        if (timeString.contains(":") && timeString.contains(" ")){
            System.out.println("using parseFormat");
            time = parseFormat.parse(timeString);
        } else if (!timeString.contains(" ") & !timeString.contains(":") && checkForAMPM(timeString) == true && timeString.substring(0, timeString.indexOf(AMorPM(timeString))).length() == 1) {
            System.out.println("using parseFormat2");
            time = parseFormat2.parse(timeString);
        } else if (checkForAMPM(timeString) == true && !timeString.contains(" ") && timeString.contains(":")) {
            System.out.println("using parseFormat3");
            time = parseFormat3.parse(timeString);
        } else if (checkForAMPM(timeString) == false && timeString.contains(":") && timeString.length() == 5) {
            System.out.println("using parseFormat4");
            time = parseFormat4.parse(timeString);
        } else if (checkForAMPM(timeString) == true && !timeString.contains(":") && timeString.contains(" ") && timeString.substring(0, timeString.indexOf(" ")).length() == 3){
            System.out.println("using parseFormat5");
            time = parseFormat5.parse(timeString);
        } else if (checkForAMPM(timeString) == true && !timeString.contains(":") && timeString.contains(" ") && timeString.substring(0, timeString.indexOf(" ")).length() == 4){
            System.out.println("using parseFormat6");
            time = parseFormat6.parse(timeString);
        } else if (!timeString.contains(":") && !timeString.contains(" ") && checkForAMPM(timeString) == true && timeString.substring(0, timeString.indexOf(AMorPM(timeString))).length() == 3){
            System.out.println("using parseFormat7");
            time = parseFormat7.parse(timeString);
        } else if (!timeString.contains(":") && !timeString.contains(" ") && checkForAMPM(timeString) == true && timeString.substring(0, timeString.indexOf(AMorPM(timeString))).length() == 4){
            System.out.println("using parseFormat8");
            time = parseFormat8.parse(timeString);
         } else if (timeString.contains(":") && !timeString.contains(" ") && checkForAMPM(timeString) == false && timeString.length() == 8){
            System.out.println("using parseFormat9");
            time = parseFormat9.parse(timeString);
        } else {
            System.out.println("using parseFormat10");
            time = parseFormat10.parse(timeString);
        }
  
        timeString = displayFormat.format(time).toString();
        return timeString;}
    
}
