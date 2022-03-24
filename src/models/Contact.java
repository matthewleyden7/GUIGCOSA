
package models;

/**
 * Handles the operations of the Contact class
 * @author Matthew Leyden
 */
public class Contact {
    
    private int contactId;
    private String contactName;
    private String contactEmail;
    
    // no argument constructor
    public Contact(){}
    
    /**
     * Argument constructor
     * @param contactId Integer contact ID
     * @param contactName String contact name
     */
    public Contact(int contactId, String contactName){
        this.contactId = contactId;
        this.contactName = contactName;
    }
    
    /**
     * To display the contact name in ComboBoxes
     * @return String of contact name
     */
    @Override  
    public String toString() {return contactName;}
    
    // getters
    public int getContactId() {return contactId;}
    public String getContactName() {return contactName;}
    public String getContactEmail() {return contactEmail;}
    
    // setters
    public void setContactId(int contactId) {this.contactId = contactId;}
    public void setContactName(String contactName) {this.contactName = contactName;}
    public void setContactEmail(String contactEmail) {this.contactEmail = contactEmail;}   
}
