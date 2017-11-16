/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leisurelog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class MarineStructure {
    
    private int marineCount;
    HashMap<Integer, Marine> hm = new HashMap<>();

    MarineStructure(File file){//, int dodid, String firstName, String lastName, int roomNumber,
            //Rank rank, Tier tier) {
        
        //BufferedReader br = new BufferedReader(new FileReader(file));
        //String[] line = br.readLine().split(", ");
        //br.close();
        //if (MarineStructure Rank : line[0] ) {
        
        //}
        
        
    }
    
    public void read() {
        try {
        FileInputStream fstream = new FileInputStream("input.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] splitOut = strLine.split(", ");
            if (splitOut.length == 6) {
                Marine marine = new Marine();
                marine.setDODID(splitOut[0]);
                System.out.println("Reading DODID: " +marine.getDODID());
                marine.setRank(splitOut[1]);
                System.out.println("Reading rank: " +marine.getRank());
                marine.setFirstName(splitOut[2]);
                System.out.println("Reading first name: " +marine.getFirstName());
                marine.setLastName(splitOut[3]);
                System.out.println("Reading last name: " +marine.getLastName());
                marine.setRoomNumber(splitOut[4]);
                System.out.println("Reading room number: " +marine.getRoomNumber());
                marine.setTierLevel(splitOut[5]);
                System.out.println("Reading tier level: " +marine.getTierLevel());
                //data.add(marine);
            } else {
                System.out.println("Invalid class: " + strLine);
            }
        }
            fstream.close();
        } catch (Exception e) {
            System.out.println("Error: " +e.getMessage());      
            }
        }
    
    public void add() {
        //todo
    }
    //method that searches for DODID 
    //public MarineStructure lookup() {
        //return marine;
    //}
    
    public MarineStructure() {
        this.marineCount = 0;
    }
    
    public int getMarineCount() {
        return marineCount;
    }

    public void setMarineCount(int marineCount) {
        this.marineCount = marineCount;
    }
    
    public void searchMarine() {
        String searchFor;
        //searchFor = l.getSearchBox(l.text);
        //System.out.println(searchFor);
        
        //if (l.data.contains(searchFor)) {
            System.out.println("I FOUND IT");
        //} else {
            System.out.println("I DID NOT FIND IT");
            
       // }
    }
}
