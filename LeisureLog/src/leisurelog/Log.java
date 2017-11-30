package leisurelog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

public class Log extends AbstractTableModel
        implements Serializable {

    // columns
    private static final int COLCNT = 7;
    private static final String[] COL = {" ", "Group", " DODID ", "     Marine     ",
        "Destination", " Check-Out ", " Check-In  "};
    //private final LogDateTime[] curfewDateTime; // curfew times
    // structure for row entries
    //private ArrayList<Object[]> logList = new ArrayList<>();
    private ArrayList<Log.Entry> logList = new ArrayList<>();
    //Object[] {boolean, LeisureGroup, Marine}
    // keep track of checked out marines
    private HashSet<Marine> outMarines = new HashSet<>();

    Log() {
        LeisureGroup.setGrpCnt(0);
    }

    @Override
    public int getRowCount() {
        return logList.size();
    }

    @Override
    public int getColumnCount() {
        return COLCNT;
    }

    // returns object applicable to column for given row, for cell display
    @Override
    public Object getValueAt(int row, int column) {
        //Object[] ob = logList.get(row);
        //LeisureGroup lg = (LeisureGroup) ob[1];
        //Marine m = (Marine) ob[2];
        Log.Entry entry = logList.get(row);
        switch (column) {
            case 0:
                //return ob[0];
                return entry.isSelected();
            case 1:
                //return lg.getID();
                return entry.getGroup().getID();
            case 2:
                //return m.getDODID();
                return entry.getMarine().getDODID();
            case 3:
                //return m;
                return entry.getMarine();
            case 4:
                //return lg.getDestination();
                return entry.getGroup().getDestination();
            case 5:
                //return lg.getChkOutTime();
                return entry.getGroup().getChkOutTime();
            case 6:
                //return lg.getChkInTime(m);
                return entry.getGroup().getChkInTime(entry.getMarine());
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COL[column];
    }

    // cell render info for table
    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return Boolean.class;
        } else if (column == 6) {
            return LogDateTime.class;
        } else {
            return Integer.class;
        }         
    }
    
    public boolean hasFlag(int row){
        return logList.get(row).hasFlag();
    }

    // only first column editable
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            // only if entry has no chk in time can select
            Log.Entry entry = logList.get(rowIndex);
            if (entry.getGroup().getChkInTime(entry.getMarine()) == null) {
                return true;
            }
        }
        return false;
    }

    // allows set of first column only
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            //logList.get(row)[0] = value;
            logList.get(row).setSelect((boolean) value);
            super.fireTableCellUpdated(row, col);
        }
    }

    // performs checks, creates new group, calls addEntries(group), returns checkout time
    public LogDateTime chkOut(Marine[] marArr, String dest, String contact)
            throws CheckoutException {
        if (marArr.length == 1) {
            if (marArr[0].getTier() != Marine.Tier.T1) {
                throw new CheckoutException(
                        "Tier Level Not Valid For Single Checkout", marArr[0]);
            }
            if (marArr[0].getGrade() != Marine.Grade.E4
                    && marArr[0].getGrade() != Marine.Grade.E5) {
                throw new CheckoutException(
                        "Rank Not Valid For Single Checkout", marArr[0]);
            }
        }
        for (Marine m : marArr) {
            if (outMarines.contains(m)) {
                throw new CheckoutException(
                        "Marine Already Checked Out", m);
            }
            if (m.getTier() == Marine.Tier.T3) {
                throw new CheckoutException(
                        "Marine Does Not Have Liberty Privileges", m);
            }
        }
        return addEntries(new LeisureGroup(marArr, dest, contact)).getChkOutTime();
    }

    // adds entries to table for group, returns the group
    private LeisureGroup addEntries(LeisureGroup lg) {
        Marine[] ma = lg.getMarines();
        for (Marine m : ma) {
            logList.add(new Log.Entry(m, lg));
            outMarines.add(m);
        }
        //for (int i = 0; i < ma.length; i++) {
        //Object[] ob = {false, lg, ma[i]};
        //logList.add(ob);

        //}
        super.fireTableRowsInserted(logList.size() - ma.length, logList.size());
        return lg;
    }

    // calls check in for table selections
    public void chkIn() {
        LogDateTime ldt = new LogDateTime();
        for (Log.Entry entry : logList) {
            if (entry.isSelected()) {
                entry.getGroup().chkIn(entry.getMarine(), ldt);
                entry.setSelect(false);
                // if passed curfew
                entry.setFlag(true);
                outMarines.remove(entry.getMarine());
            }
        }
        super.fireTableRowsUpdated(0, logList.size());
        //for (int i = 0; i < logList.size(); i++) {
        //  Object[] ob = logList.get(i);
        //if ((boolean) ob[0]) {
        //  LeisureGroup lg = (LeisureGroup) ob[1];
        //Marine m = (Marine) ob[2];
        //lg.chkIn(m, ldt);
        //ob[0] = false; //deselect checkbox
        //super.fireTableCellUpdated(i, 0);
        //super.fireTableCellUpdated(i, 6);
        //}
        // }
    }

    // export log to file
    public File export()
            throws IOException {
        LogDateTime pubLdt = new LogDateTime();
        String publishTime = pubLdt.getDate().replaceAll("/", "") + "_"
                + pubLdt.getTime().replaceAll(":", "");
        Path dir = Paths.get("LeisureLogs");
        if (!Files.isDirectory(dir)) {
            Files.createDirectory(dir);
        }
        File f = Paths.get(dir.toString(), publishTime + "_Log.txt").toFile();
        PrintWriter pw = new PrintWriter(f);
        pw.println("Leisure Log");
        pw.println("Published: " + pubLdt);
        pw.println("Duty: "); // + on duty marine;
        pw.println("Entries: " + logList.size());
        pw.println("Flags: " + 0);
        pw.println();
        pw.printf("%6s | %10s | %25s | %20s | %20s | %20s |%n", "Group", "DODID", "Marine", "Desination",
                "Check-Out", "Check-In");
        pw.println(String.format("%114s", "").replace(' ', '-'));
        for (int i = 0; i < logList.size(); i++) {
            pw.printf("%6s | %10s | %25s | %20s | %20s | %20s |%n",
                    getValueAt(i, 1), getValueAt(i, 2), getValueAt(i, 3),
                    getValueAt(i, 4), getValueAt(i, 5), getValueAt(i, 6));
        }
        pw.close();
        f.setReadOnly();
        logList = new ArrayList<>();
        super.fireTableDataChanged();
        LeisureGroup.setGrpCnt(0);
        return f;
    }

    private static class Entry implements Serializable {

        private final Marine m;
        private final LeisureGroup lg;
        private boolean flag, selected;

        Entry(Marine m, LeisureGroup lg) {
            this.m = m;
            this.lg = lg;
            selected = false;
            flag = false;
        }

        //getters
        public Marine getMarine() {
            return m;
        }

        public LeisureGroup getGroup() {
            return lg;
        }

        public boolean isSelected() {
            return selected;
        }

        public boolean hasFlag() {
            return flag;
        }

        //setters
        public void setSelect(boolean select) {
            this.selected = select;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }

}
