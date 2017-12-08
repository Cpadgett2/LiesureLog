package leisurelog;

import java.io.Serializable;

/**
 * Marine Class defines Marine objects
 *
 * @author TeamLeisure
 */
public class Marine implements Serializable {

    private static final long serialVersionUID = -5901364777177774222L;

    // Marine info
    private Grade grade;
    private Tier tier;
    private int roomNumber;
    // marine info that cannot be updated
    private final String firstName;
    private final String midName;
    private final String lastName;
    private final long dodid;

    // all info constructor
    Marine(long id, Grade grade, String firstName, String mid,
            String lastName, int room, Tier tier) {
        this.dodid = id;
        this.grade = grade;
        this.firstName = firstName;
        this.midName = mid;
        this.lastName = lastName;
        this.roomNumber = room;
        this.tier = tier;
    }

    // construct without optional middle name 
    Marine(long id, Grade grade, String firstName,
            String lastName, int room, Tier tier) {
        this.dodid = id;
        this.grade = grade;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = room;
        this.tier = tier;
        this.midName = "";
    }

    public enum Grade {
        E1, E2, E3, E4, E5
    }

    public enum Tier {
        T1, T2, T3
    }

    // getters
    public long getDODID() {
        return dodid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMid() {
        return midName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public Grade getGrade() {
        return grade;
    }

    public Tier getTier() {
        return tier;
    }

    public String getRank() {
        switch (grade) {
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

    public String getName() {
        return lastName + ", " + firstName + " " + midName;
    }

    // setters
    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public void setTier(Tier t) {
        this.tier = t;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    // returns string for marine data file export
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s%n", dodid, grade, firstName,
                midName, lastName, roomNumber, tier);
    }

    // string for display
    @Override
    public String toString() {
        return getRank() + " " + getName() + " " + roomNumber + " " + tier;
    }
}
