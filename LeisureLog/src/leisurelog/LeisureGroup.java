
package leisurelog;


public class LeisureGroup {
    //total number of groups
    private static int grpCnt;
    // this groups id
    private final int grpID;
    //destination
    private final String dest;
    // common check out time
    private final LogDateTime chkOut;
    // marine array for group members
    private final Marine[] marArr;
    // check in time arrary 1 for 1 to marine array for different check ins
    private LogDateTime[] chkIn;
    
    // construct from marine array of memebers, destination, and check out time
    LeisureGroup(Marine[] ma, String dest, LogDateTime chkOut){
        this.marArr=ma;
        this.chkIn= new LogDateTime[ma.length];
        this.dest=dest;
        this.chkOut=chkOut;
        grpID = ++grpCnt;
    }
    
    //getters
    public int getID(){return grpID;}
    public String getDestination(){return dest;}
    public LogDateTime getChkOutTime(){return chkOut;}
    public Marine[] getMarines(){return marArr;}
    public int getGrpCnt(){return marArr.length;}
    
    // gets check in time for specified marines
    public LogDateTime getChkInTime(Marine m){
        for (int i = 0;i<marArr.length;i++){
            if(marArr[i].equals(m)){
                return chkIn[i];
            }
        }
        return null;
    }    
    
    // checks in specified marine, needs checks
    // public boolean chkIn(ArrayList<Marine> mal)
    public void chkIn(Marine m){
        for (int i = 0;i<marArr.length;i++){
            if(marArr[i].equals(m)){
                chkIn[i] = new LogDateTime();
            }
        }
    }
    
}
