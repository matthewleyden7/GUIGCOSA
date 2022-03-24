
package models;

import database.DBCustomer;
import java.sql.SQLException;
import java.util.Map;

/**
 * Handles the operations of the Customer class
 * @author Matthew Leyden
 */
public class Customer {

    private int id;
    private String name;
    private String address;
    private String zip;
    private String phone;
    private int divisionId;
    private int countryId;
    private String stateName;
    private String countryName;
    private State state;
    private Country country;
    
    Map<Integer, String> countryMap = Map.ofEntries(Map.entry(1, "U.S"), Map.entry(2, "UK"), Map.entry(3, "Canada"));
    
    // no argument constructor
    public Customer(){};

    /**
     * Argument Constructor
     * @param Id Customer ID
     * @param name Customer name
     * @param address Customer address
     * @param zip Customer postal code
     * @param phone Customer phone number
     * @param divisionId Division ID of Customer
     */
    public Customer(int Id, String name, String address, String zip, String phone, int divisionId) throws SQLException{
        
        this.id = Id; 
        this.name = name; 
        this.address = address; 
        this.zip = zip;
        this.phone = phone; 
        this.divisionId = divisionId;
    }
    
    /**
     * To display customer name in ComboBoxes
     * @return String of Customer name
     */
    @Override
    public String toString(){return name;}
    
    // getters
    public int getId() {return this.id;}
    public String getName() {return this.name;}
    public String getAddress() {return this.address;}
    public String getZip() {return this.zip;}
    public String getPhone() {return this.phone;}
    public int getDivisionId(){return this.divisionId;}
    public String getStateName(){return this.stateName;}
    public String getCountryName(){return this.countryName;}
    public State getState() {return this.state;}
    public Country getCountry(){return this.country;}
    
    // setters
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setAddress(String address) {this.address = address;}
    public void setZip(String zip) {this.zip = zip;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setDivisionId(int divisionId){this.divisionId = divisionId;}
    public void setState(State state){this.state = state; this.stateName = state.getState();}
    public void setCountry(Country country){this.country = country; this.countryName = country.getCountry();}
    

    /**
     * Used to create state and country classes and associate with customer
     */
    public void setupState() throws SQLException {
        this.state = DBCustomer.getState(this.divisionId); 
        this.stateName = this.state.getState(); 
        this.countryId = this.state.getCountryId(); 
        this.countryName = countryMap.get(this.state.getCountryId());
        this.country = new Country(state.getCountryId(), this.countryName);}

}
