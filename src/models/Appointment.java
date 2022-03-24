
package models;

import controllers.LoginFormController;
import database.DBAppointment;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Handles the operations of the Appointment class
 * @author Matthew Leyden
 */
public class Appointment {
    
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private int customerId;
    private int contactId;
    private Contact contact;
    private String contactName;
    private Customer customer;
    private String customerName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ZonedDateTime utcZDT;
    private ZonedDateTime localStartZDT;
    private ZonedDateTime localEndZDT;
    public ZonedDateTime localZonedDT = LoginFormController.localZonedDT;
    public ZoneId localZoneId = LoginFormController.localZoneId;

    
    // no argument constructor
    public Appointment() {}
    
    // argument constructor 
    /**
     * Argument constructor
     * <p>Conversion of Start string from UTC format to Local format for display purposes</p>
     * <p>Conversion of End String from UTC format to Local format for display purposes</p>
     * @param id Integer appointment ID
     * @param title Appointment title
     * @param description Appointment description
     * @param location Appointment location
     * @param type Appointment type
     * @param start String of appointment start date and time
     * @param end String of appointment end date and time
     * @param customerId Customer ID
     * @param contactId Contact ID
     */
    public Appointment(int id, String title, String description, String location, String type, String start, String end, int customerId, int contactId){
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.customerId = customerId;
        this.contactId = contactId;
        
        // conversion of Start from UTC format to Local format for display purposes
        LocalDateTime LDT = LocalDateTime.parse(start.substring(0, 10) + "T" + start.substring(11, start.length()));
        this.utcZDT = ZonedDateTime.of(LDT, ZoneOffset.UTC);
        String stringLocal = this.utcZDT.toInstant().atZone(this.localZoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
       
        this.startDate = stringLocal.substring(0, 10);
        this.startTime = stringLocal.substring(11, stringLocal.length());
        this.startDateTime = LocalDateTime.parse(startDate + "T" + startTime);
        this.localStartZDT = ZonedDateTime.of(this.startDateTime, localZoneId);
        this.start = start;
        
         // conversion of End from UTC format to Local format for display purposes
        this.endDateTime = LocalDateTime.parse(end.substring(0, 10) + "T" + end.substring(11, end.length()));
        this.utcZDT = ZonedDateTime.of(this.endDateTime, ZoneOffset.UTC);
   
        String stringLocal2 = this.utcZDT.toInstant().atZone(this.localZoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.endDate = stringLocal2.substring(0, 10);
        this.endTime = stringLocal2.substring(11, stringLocal.length());
        this.endDateTime = LocalDateTime.parse(endDate + "T" + endTime);
        this.localEndZDT = ZonedDateTime.of(this.endDateTime, localZoneId);
        this.end = end;
    }

    // Getters
    public int getId() {return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public String getLocation() {return location;}
    public String getType() {return type;}
    public String getStart() {return start;}
    public String getEnd() {return end;}
    public String getStartDate() {return startDate;}
    public String getStartTime() {return startTime;}
    public String getEndDate() {return endDate;}
    public String getEndTime() {return endTime;}
    public LocalDateTime getStartDateTime() {return this.startDateTime;}
    public LocalDateTime getEndDateTime() {return this.endDateTime;}
    public ZonedDateTime getLocalStartZDT() {return this.localStartZDT;}
    public ZonedDateTime getLocalEndZDT() {return this.localEndZDT;}
    public int getCustomerId() {return customerId;}
    public int getContactId() {return contactId;}
    public String getContactName() {return contactName;}
    public String getCustomerName() {return customerName;}
    public Contact getContact() {return contact;}
    public Customer getCustomer() {return customer;}

    // Setters
    public void setId(int id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setLocation(String location) {this.location = location;}
    public void setType(String type) {this.type = type;}
    public void setStart(String start) {this.start = start;}
    public void setEnd(String end) {this.end = end;}
    public void setStartTime(String startTime) {this.startTime = startTime;}
    public void setStartDate(String startDate) {this.startDate = startDate;}
    public void setEndTime(String endTime) {this.endTime = endTime;}
     public void setEndDate(String endDate) {this.endDate = endDate;}
    public void setCustomerId(int customerId) {this.customerId = customerId;}
    public void setContactId(int contactId) {this.contactId = contactId;}
    public void setCustomerName(String customerName) {this.customerName = customerName;}
    
    // set contact and customer
    public void setContact(Contact contact) {this.contact = contact;}
    public void setCustomer(Customer customer) {this.customer = customer; this.customerId = customer.getId();}
    
 
    /**
     * Sets a Contact object to be associated with the appointment using contactId
     */
    public void setupContact() throws SQLException {this.contact = DBAppointment.getContact(this.contactId); this.contactName = contact.getContactName();}
   
     /**
     * Sets a Customer object to be associated with the appointment using customerId
     */
    public void setupCustomer() throws SQLException {this.customer = DBAppointment.getCustomer(this.customerId); this.customerName = customer.getName();}
    
    
 
    

    
    
    
}
