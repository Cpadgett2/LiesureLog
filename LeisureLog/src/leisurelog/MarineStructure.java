/**
 * Marine Structure Class
 * Contains backing structure for Marine management
 *
 * @author TeamLeisure
 */
package leisurelog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MarineStructure {

    // marines stored in hashmap, lookup based on dodid key
    HashMap<Long, Marine> hm = new HashMap<>(); 

    // builds structure from marine data file
    public void build(File file) throws IOException {
        long l;
        String rank, firstName, midName, lastName, tier;
        int roomNumber;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] splitOut = strLine.split(",");
            if (splitOut.length == 7) {
                l = Long.parseLong(splitOut[0]);
                rank = splitOut[1];
                firstName = splitOut[2].trim();
                midName = splitOut[3].trim();
                lastName = splitOut[4].trim();
                roomNumber = Integer.parseInt(splitOut[5].trim());
                tier = splitOut[6].trim();
                Marine.Grade grade1 = Marine.Grade.valueOf(rank.trim());
                Marine.Tier tier1 = Marine.Tier.valueOf(tier.trim());
                Marine marine = new Marine(l, grade1, firstName, midName,
                        lastName, roomNumber, tier1);
                hm.put(l, marine);
            }
        }
        br.close();
    }
    
    // clears then builds
    public void reBuild(File file) throws IOException {
        hm = new HashMap<>();
        build (file);
    }

    // exports marines to csv file
    public void toFile(File file) 
            throws IOException {
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        Iterator<Map.Entry<Long, Marine>> it = hm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Marine> pairs = it.next();
            out.write(pairs.getValue().toFileString());
        }
        out.close();
    }

    // adds marine to structure
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

    // removes marine from structure
    public boolean remove(Marine m) {
        return remove(m.getDODID());
    }

    // removes marine associated with dodid key from stucture 
    public boolean remove(long dodid) {
        if (hm.remove(dodid) == null) {
            return false;
        } else {
            return true;
        }
    }

    // how many maines in hashmap
    public int getMarineCount() {
        return hm.size();
    }

}
