/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leisurelog;

/**
 *
 * @author 1289119228A
 */
public class Marine {
    private String rank;
    private String firstName;
    private String lastName;
    private String dodid;
    private String roomNumber;
    private String tierLevel;
    
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getDODID() {
        return dodid;
    }

    public void setDODID(String dodid) {
        this.dodid = dodid;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public String getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(String tierLevel) {
        this.tierLevel = tierLevel;
    }
    
    @Override
    public String toString() {
        return(dodid + " "+rank + " " +firstName +" " +lastName +" " +roomNumber +" " +tierLevel);
                
    }
    
}
    
    
