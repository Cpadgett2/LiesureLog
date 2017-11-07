
package leisurelog;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class Log extends AbstractTableModel {
    // columns
    private static final int colCnt = 7;
    private static final String[] col = {" ", "Group", " DODID ", "     Marine     ",
        "Destination", " Check-Out ", " Check-In  "};

    // structure for row entries
    private ArrayList<Object[]> logList = new ArrayList<>();
    //Object[] {boolean, LeisureGroup, Marine}
    
    @Override
    public int getRowCount(){
        return logList.size();
    }
    
    @Override
    public int getColumnCount(){
        return colCnt;
    }
    
    // returns object applicable to column for given row, for cell display
    @Override
    public Object getValueAt(int row, int column){
        Object[] ob = logList.get(row);
        LeisureGroup lg = (LeisureGroup)ob[1];
        Marine m = (Marine)ob[2];
        switch(column){
            case 0:
                return ob[0];
            case 1:
                return lg.getID();
            case 2:
                return m.getDODID();
            case 3:
                return m;
            case 4:
                return lg.getDestination();
            case 5:
                return lg.getChkOutTime();
            case 6:
                return lg.getChkInTime(m);
            default :
                return null;            
        }
    }
    
    @Override
    public String getColumnName(int column){
        return col[column];
    }
    
    // cell render info for table
    @Override
    public Class getColumnClass(int column){
        if (column == 0){
            return Boolean.class;
        } else {//if (column <= 2){
            return Integer.class;
        } //else {
            //return String.class;
        //}        
    }
    
    // sets first column only editable
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        if (columnIndex == 0){return true;}
        return false;
    }
    
    // allows set of first column only
    @Override
    public void setValueAt(Object value, int row, int col){
        if(col == 0){
            logList.get(row)[0] = value;
            super.fireTableCellUpdated(row, col);
        }
    }
    
    // adds checkout group to table
    public void chkOut(LeisureGroup lg){
        Marine[] ma = lg.getMarines();
        for (int i = 0; i<ma.length;i++){
            Object[] ob = {false, lg, ma[i]};
            logList.add(ob);
        } 
        super.fireTableRowsInserted(logList.size()-ma.length, logList.size());
    }
    
    // calls check in for table selections
    public void chkIn(){
        for (int i =0;i<logList.size();i++){
            Object[] ob = logList.get(i);
            if ((boolean)ob[0]){
                LeisureGroup lg = (LeisureGroup)ob[1];
                Marine m = (Marine)ob[2];
                lg.chkIn(m);
                super.fireTableCellUpdated(i, 6);
            }
        }
    }
    

}
