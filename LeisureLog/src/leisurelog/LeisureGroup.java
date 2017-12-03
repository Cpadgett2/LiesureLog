package leisurelog;

import java.io.Serializable;
import java.util.Arrays;

public class LeisureGroup implements Comparable<LeisureGroup>, Serializable {
    private static final long serialVersionUID = -631615550959333287L;
    //total number of groups
    private static int grpCnt;
    // this groups id
    private final int grpID;
    //destination
    private final String dest;
    // contact number
    private final String contact;
    // common check out time
    private final LogDateTime chkOut;
    // marine array for group members
    private final Marine[] marArr;
    // curfew[1] E4/E5 T1, curfew[0] others
    //private final LogDateTime[] curfew;
    // check in time arrary 1 for 1 to marine array for different check ins
    private LogDateTime[] chkIn;
    

    // construct from marine array of memebers, destination, check out current time
    LeisureGroup(Marine[] ma, String dest, String contact) {
        this.marArr = ma;
        this.chkIn = new LogDateTime[ma.length];
        this.dest = dest;
        this.contact = contact;
        this.chkOut = new LogDateTime();
        //curfew = chkOut.getCurfews();
        grpID = ++grpCnt;
    }

    //getters
    public int getID() {
        return grpID;
    }

    public String getDestination() {
        return dest;
    }

    public LogDateTime getChkOutTime() {
        return chkOut;
    }

    public Marine[] getMarines() {
        return marArr;
    }
    
    public String getContact(){
        return contact;
    }
    
    public static int getGrpCnt() {
        return grpCnt;
    }
    
    
    public static void setGrpCnt(int i){
        grpCnt=i;
    }

    // gets check in time for specified marines
    public LogDateTime getChkInTime(Marine m) {
        for (int i = 0; i < marArr.length; i++) {
            if (marArr[i].equals(m)) {
                return chkIn[i];
            }
        }
        return null;
    }
    


    // checks in specified marine
    public void chkIn(Marine m, LogDateTime ldt) {
        for (int i = 0; i < marArr.length; i++) {
            if (marArr[i].equals(m)) {
                chkIn[i] = ldt;
                return;               
            }
        }
        return;
    }
    
    @Override
    public int compareTo(LeisureGroup lg){
        return this.grpID - lg.getID();
    }
    
    @Override
    public String toString(){
        return "Group " + grpID;
    }

}
