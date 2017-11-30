/**
 * Marine Structure Class
 * Contains backing structure for Marine management
 *
 * @author TeamLeisure
 */
package leisurelog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MarineStructure {

    private int marineCount;
    HashMap<Long, Marine> hm = new HashMap<>();

    MarineStructure(File file)
            throws IOException {
        this();
        build(file);
    }

    MarineStructure() {
        this.marineCount = 0;
    }

    public final void build(File file) throws IOException {
        long l;
        String rank, firstName, lastName, tier;
        int roomNumber;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] splitOut = strLine.split(",");
            if (splitOut.length == 6) {
                l = Long.parseLong(splitOut[0]);
                rank = splitOut[1];
                firstName = splitOut[2].trim();
                lastName = splitOut[3].trim();
                roomNumber = Integer.parseInt(splitOut[4].trim());
                tier = splitOut[5].trim();
                Marine.Grade grade1 = Marine.Grade.valueOf(rank.trim());
                Marine.Tier tier1 = Marine.Tier.valueOf(tier.trim());
                Marine marine = new Marine(l, grade1, firstName, lastName, roomNumber, tier1);
                hm.put(l, marine);
                marineCount++;
            }
        }
        br.close();
    }

    public boolean add(Marine m) {
        if (hm.containsKey(m.getDODID())) {
            return false;
        }
        hm.put(m.getDODID(), m);
        return true;
    }

    //method that lookups up Marine for DODID key 
    public Marine lookup(long dodid) {
        return hm.get(dodid);
    }

    public boolean remove(Marine m) {
        return remove(m.getDODID());
    }

    public boolean remove(long dodid) {
        if (hm.remove(dodid) == null) {
            return false;
        } else {
            return true;
        }
    }

    public int getMarineCount() {
        return marineCount;
    }

}
