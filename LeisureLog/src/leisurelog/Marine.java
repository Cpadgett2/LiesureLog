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
    private String firstName;
    private String midName;
    private String lastName;
    private long dodid;
    private int roomNumber;

    Marine() {
        marineNum = cnt++;
    }
    
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

    public Marine(int dodid) {
        marineNum = cnt++;
        this.dodid = dodid;
    }

    public enum Grade {
        E1, E2, E3, E4, E5
    }

    public enum Tier {
        T1, T2, T3
    }

    // test purposes
    public void test() {
        System.out.println(dodid);
        System.out.println(grade);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(roomNumber);
        System.out.println(tier);
    }

    public long getDODID() {
        return dodid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMid(){
        return midName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDODID(int dodid) {
        this.dodid = dodid;
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
        //return "Marine " + marineNum;
        //return(dodid + " "+rank + " " +firstName +" " +lastName +" " +roomNumber +" " +tierLevel);
    }
}
