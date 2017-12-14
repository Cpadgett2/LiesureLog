package leisurelog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

public class Log extends AbstractTableModel {

    private static final long serialVersionUID = -2814358752748728351L;
    // columns
    private static final int COLCNT = 8;
    private static final String[] COL = {"  ", "Group", "  DODID  ", "      Marine      ",
        " Destination ", " Contact ", " Check-Out ", " Check-In  "};
    // structure for row entries
    private ArrayList<Log.Entry> logList = new ArrayList<>();
    private int flagCnt;
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

    // returns number marines currently checked out
    public int getOutCnt() {
        return outMarines.size();
    }

    // returns object applicable to column for given row, for cell display
    @Override
    public Object getValueAt(int row, int column) {
        Log.Entry entry = logList.get(row);
        switch (column) {
            case 0:
                return entry.isSelected();
            case 1:
                return entry.getGroup().getID();
            case 2:
                return entry.getMarine().getDODID();
            case 3:
                return entry.getMarine();
            case 4:
                return entry.getGroup().getDestination();
            case 5:
                return entry.getGroup().getContact();
            case 6:
                return entry.getGroup().getChkOutTime();
            case 7:
                return entry.getGroup().getChkInTime(entry.getMarine());
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COL[column];
    }

    // returns what class to display column as
    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return Boolean.class;
        } else if (column == 7) {
            return LogDateTime.class;
        } else {
            return Integer.class;
        }
    }

    // true if log entry has late flag
    public boolean hasFlag(int row) {
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
        super.fireTableRowsInserted(logList.size() - ma.length, logList.size());
        return lg;
    }

    // calls check in for table selections
    public LogDateTime chkIn() {
        boolean check = false;
        LogDateTime ldt = new LogDateTime();
        for (Log.Entry entry : logList) {
            if (entry.isSelected()) {
                check = entry.getGroup().chkIn(entry.getMarine(), ldt) || check;
                entry.setSelect(false);
                // if passed curfew
                LogDateTime[] curfew
                        = entry.getGroup().getChkOutTime().getCurfews();
                if (ldt.isAfter(curfew[1])) {
                    entry.setFlag(true);
                    flagCnt++;
                }
                Marine m = entry.getMarine();
                if (ldt.isAfter(curfew[0])) {
                    if ((m.getGrade() != Marine.Grade.E4
                            && m.getGrade() != Marine.Grade.E5)
                            || m.getTier() != Marine.Tier.T1) {
                        entry.setFlag(true);
                        flagCnt++;
                    }
                }
                outMarines.remove(entry.getMarine());
            }
        }
        super.fireTableRowsUpdated(0, logList.size());
        // if marine checked in success return check in time
        if (check) {
            return ldt;
        } else {
            return null;
        }
    }

    // publish log to files
    public File[] export(Marine duty, Path directory)
            throws IOException {
        Collections.sort(logList);
        LogDateTime pubLdt = new LogDateTime();
        String publishTime = pubLdt.getDate().replaceAll("/", "") + "_"
                + pubLdt.getTime().replaceAll(":", "");
        Path filePath = Paths.get(directory.toString(),
                "log_" + publishTime);
        File[] exportFiles = new File[2];
        // call to publish to csv
        exportFiles[0] = toCSV(filePath, pubLdt, duty);
        // call to publish to text
        exportFiles[1] = toTxt(filePath, pubLdt, duty);
        File archive = Paths.get(directory.toString(), "log_archive.csv").toFile();
        archive.setWritable(true);
        // call to archive log
        appendEntries(archive);
        // set permissions
        archive.setWritable(false, false);
        for (File f : exportFiles) {
            f.setWritable(false, false);
            f.setReadOnly();
        }
        // reset log
        logList = new ArrayList<>();
        flagCnt = 0;
        LeisureGroup.setGrpCnt(0);
        super.fireTableDataChanged();
        return exportFiles;
    }

    // publishes text file variation of log
    private File toTxt(Path filePath, LogDateTime pubTime, Marine duty)
            throws IOException {
        File file = Paths.get(filePath.toString() + ".txt").toFile();
        PrintWriter pw = new PrintWriter(file);
        pw.println("Leisure Log");
        pw.println("Published: " + pubTime);
        pw.println("Duty: " + duty);
        pw.println("Entries: " + logList.size());
        pw.println("Flags: " + flagCnt);
        pw.println();
        pw.printf("%6s | %10s | %30s | %20s | %18s | %18s |", "Group", "DODID",
                "Marine", "Desination", "Check-Out", "Check-In");
        pw.println();
        pw.println(String.format("%118s", "").replace(' ', '-'));
        for (int i = 0; i < logList.size(); i++) {
            pw.printf("%6s | %10s | %30s | %20s | %18s | %18s | ",
                    getValueAt(i, 1), getValueAt(i, 2), getValueAt(i, 3),
                    getValueAt(i, 4), getValueAt(i, 6), getValueAt(i, 7));
            if (logList.get(i).hasFlag()) {
                pw.printf("%s", "Late Check-In");
            }
            pw.println();
        }
        pw.close();
        return file;
    }

    // publishes csv variation of log
    private File toCSV(Path filePath, LogDateTime pubTime, Marine duty)
            throws IOException {
        File file = Paths.get(filePath.toString() + ".csv").toFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("Leisure Log" + System.lineSeparator());
        bw.write("Published: " + pubTime + System.lineSeparator());
        bw.write("Duty: " + duty.toString().replaceAll(",", "")
                + System.lineSeparator());
        bw.write("Entries: " + logList.size() + System.lineSeparator());
        bw.write("Flags: " + flagCnt + System.lineSeparator());
        bw.write(System.lineSeparator());
        bw.write("Group,DODID,Marine,Destination,Check-Out,Check-In"
                + System.lineSeparator());
        bw.close();
        appendEntries(file);
        return file;
    }

    // appends entries to file parameter
    private void appendEntries(File file)
            throws IOException {
        boolean exists = Files.exists(file.toPath());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        if (!exists) {
            bw.write("Group,DODID,Marine,Destination,Check-Out,Check-In"
                + System.lineSeparator());
        }
        for (Entry e : logList) {
            bw.write(e.toString());
            bw.write(System.lineSeparator());
        }
        bw.close();
    }

    // sorts log entries based on column 
    public void sort(int col) {
        switch (col) {
            case 0:
                break;
            case 1:
                logList.sort((Entry e1, Entry e2)
                        -> e1.getGroup().compareTo(e2.getGroup()));
                break;
            case 2:
                logList.sort((Entry e1, Entry e2)
                        -> Long.compare(e1.getMarine().getDODID(),
                                e2.getMarine().getDODID()));
                break;
            case 3:
                logList.sort((Entry e1, Entry e2)
                        -> e1.getMarine().compareTo(e2.getMarine()));
                break;
            case 4:
                logList.sort((Entry e1, Entry e2)
                        -> e1.getGroup().getDestination().compareTo(
                                e2.getGroup().getDestination()));
                break;
            case 5:
                logList.sort((Entry e1, Entry e2)
                        -> e1.getGroup().getContact().compareTo(
                                e2.getGroup().getContact()));
                break;
            case 6:
                logList.sort((Entry e1, Entry e2)
                        -> e1.compareTo(e2));
                break;
            case 7:
                logList.sort((Entry e1, Entry e2)
                        -> {
                    if (e1.getGroup().getChkInTime(e1.getMarine()) == null) {
                        return -1;
                    }
                    return e1.getGroup().getChkInTime(e1.getMarine()).compareTo(
                            e2.getGroup().getChkInTime(e2.getMarine()));
                });
                break;
        }
        super.fireTableDataChanged();
    }

    // inner class is Entry for Log
    private static class Entry implements Serializable, Comparable<Entry> {

        private static final long serialVersionUID = -4399433595070230812L;
        private final Marine m;
        private final LeisureGroup lg;
        private boolean selected = false, lateFlag = false;

        // construct with marine and marines group
        Entry(Marine m, LeisureGroup lg) {
            this.m = m;
            this.lg = lg;
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
            return lateFlag;
        }

        //setters
        public void setSelect(boolean select) {
            this.selected = select;
        }

        public void setFlag(boolean flag) {
            this.lateFlag = flag;
        }

        // natural ordering for log entries on checkout time
        @Override
        public int compareTo(Entry e) {
            return this.lg.getChkOutTime().compareTo(e.getGroup().getChkOutTime());
        }

        @Override
        public String toString() {
            String str = lg.getID() + "," + m.getDODID() + ","
                    + m.toString().replaceAll(",", "") + ","
                    + lg.getDestination() + ","
                    + lg.getChkOutTime() + "," + lg.getChkInTime(m);
            if (lateFlag) {
                str = str + ",Late Check-In";
            }
            return str;
        }
    }

}
