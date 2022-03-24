package models;

/**
 * Handles the operations of the State class
 * @author Matthew Leyden
 */
public class State {
    
    private int divisionId;
    private String state;
    private int countryId;
    
    // no argument constructor
    public State(){};
    
    /**
     * Argument constructor
     * @param divisionId Integer division ID
     * @param state State name
     * @param countryId Country ID
     */
    public State(int divisionId, String state, int countryId) {
        this.divisionId = divisionId;
        this.state = state;
        this.countryId = countryId;
    }
    
    /**
     * To display the state name in ComboBoxes
     * @return String of state name
     */
    @Override
    public String toString() {return state;}
    
    // getters
    public int getDivisionId() {return divisionId;}
    public String getState() {return state;}
    public int getCountryId() {return countryId;}
    
    // setters
    public void setDivisionId(int divisionId) {this.divisionId = divisionId;}
    public void setState(String state) {this.state = state;}
    public void setCountryId(int countryId) {this.countryId = countryId;}

}