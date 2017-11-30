package leisurelog;

import java.io.Serializable;

/**
 * Marine Class
 *
 * @author TeamLeisure
 */
public class Marine implements Serializable {

    private static int cnt;
    private int marineNum;
    private Grade grade;
    private Tier tier;
    private int roomNumber;
    private final String firstName;
    private final String midName;
    private final String lastName;
    private final long dodid;
    

    Marine(long id, Grade grade, String firstName, String mid,
            String lastName, int room, Tier tier ){
        this.dodid = id;
        this.grade = grade;
        this.firstName = firstName;
        this.midName = mid;
        this.lastName = lastName;
        this.roomNumber = room;
        this.tier = tier;
        marineNum = cnt++;
    }
    
    Marine(long id, Grade grade, String firstName,
            String lastName, int room, Tier tier ){
        this.dodid = id;
        this.grade = grade;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = room;
        this.tier = tier;
        this.midName = "";
        marineNum = cnt++;
    }

    public enum Grade {
        E1, E2, E3, E4, E5
    }

    public enum Tier {
        T1, T2, T3
    }

    public long getDODID() {
        return dodid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMid(){
        return midName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
    
    public Grade getGrade(){
        return grade;
    }
    
    public void setGrade(Grade grade){
        this.grade = grade;
    }
    
    public Tier getTier(){
        return tier;
    }
    
    public String getRank(){
        switch (grade){
            case E1:
                return "Pvt";
            case E2:
                return "PFC";
            case E3:
                return "LCpl";
            case E4:
                return "Cpl";
            case E5:
                return "Sgt";
            default:
                return null;
        }
    }

    public void setTier(Tier t){
        this.tier = t;
    }
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public String getName(){
        return lastName + ", " + firstName + " " + midName;
    }

    @Override
    public String toString() {
        return getRank() + " " + getName() + " " + roomNumber + " " + tier; 
    }
}
