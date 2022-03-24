package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Handles the operations of the Country class
 * @author Matthew Leyden
 */
public class Country {
    
    private int countryId;
    private String country;
    
    // no argument constructor
    public Country(){};
        
    /**
     * Argument constructor
     * @param countryId Country ID
     * @param country Country name
     */
    public Country(int countryId, String country) {
    this.countryId = countryId;
    this.country = country;}

    /**
     * To display the country name in ComboBoxes
     * @return String of country name
     */
    @Override
    public String toString() {return country;}

    // getters
    public int getCountryId() {return countryId;}
    public String getCountry() {return country;}
    
    // setters
    public void setCountryId(int countryId) {this.countryId = countryId;}
    public void setCountry(String country) {this.country = country;}
}