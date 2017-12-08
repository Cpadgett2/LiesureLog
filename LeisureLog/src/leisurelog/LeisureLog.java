package leisurelog;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Main Frame display for logging GUI, startup actions, contains main()
 *
 * @author TeamLeisure
 */
public class LeisureLog extends JFrame {

    private static final long serialVersionUID = 6507332290704320252L;
    //marine stucture
    private MarineStructure ms = new MarineStructure();
    // top pannels 
    private LookupPanel lkPan = new LookupPanel(ms);
    private ListPanel listPan = new ListPanel();
    private CheckPanel chkPan = new CheckPanel();
    // log is model for table
    private Log log;
    private JTable table;
    // paths for configuration
    private static Path logDirectoryPath, marineFilePath;
    // On-duty Marine
    private Marine duty;

    //constructor
    LeisureLog() {
        super("Leisure Log");
        this.setSize(750, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initilize();
        bldGUI();
        this.setVisible(true);
    }

    // main method instantiates 
    public static void main(String[] args) throws IOException {
        new LeisureLog();
    }

    // program initialization, read Marine Data file, log recovery
    private void initilize() {
        // attempts to read from config file
        try (Scanner sc = new Scanner(new File("log.ini"))) {
            String marineFile = "", logDirectory = "";
            while (sc.hasNextLine()) {
                String[] str = sc.nextLine().split("=");
                if (str[0].equalsIgnoreCase("marine_data_file")) {
                    if (str.length == 2) {
                        marineFile = str[1].trim();
                    }
                } else if (str[0].equalsIgnoreCase("log_directory")) {
                    if (str.length == 2) {
                        logDirectory = str[1].trim();
                    }
                }
            }
            marineFilePath = Paths.get(marineFile);
            logDirectoryPath = Paths.get(logDirectory);
            boolean configFlag = false;
            // if paths were read check validity
            if (!Files.isReadable(marineFilePath)) {
                errMessage(this, "Error Reading Marine Data File\n"
                        + marineFilePath.toString());
                configFlag = true;
                marineFilePath = null;
            }
            if (!Files.isDirectory(logDirectoryPath)
                    || logDirectoryPath.toString().isEmpty()) {
                errMessage(this, "Unable To Find Log Directory\n"
                        + logDirectoryPath.toString());
                configFlag = true;
                logDirectoryPath = null;
            }

            // if problem with file display config frame
            if (configFlag) {
                new ConfigFrame(ms);
            } else {
                ms.build(marineFilePath.toFile());
            }
        } catch (NullPointerException | IOException e) {
            // if proble reading config file display config frame
            new ConfigFrame(ms);
        }
        // recover log, set table model
        log = recoverLog();
        table = new JTable(log);
    }

    // attempts log recovery from file, returns new log if backup empty
    private Log recoverLog() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("log.ser"))) {
            int backupGrpCnt = ois.readInt();
            if (backupGrpCnt < 1) {
                return new Log();
            }
            LeisureGroup.setGrpCnt(backupGrpCnt);
            return (Log) ois.readObject();
        } catch (IOException exc) {
            return new Log();
        } catch (ClassNotFoundException cnf) {
            return new Log();
        }
    }

    // add components to frame for GUI construct
    private void bldGUI() {
        // Menu bar construct
        JMenuBar jmb = new JMenuBar();
        JMenu adminMenu = new JMenu("Admin"),
                helpMenu = new JMenu("Help");
        JMenuItem manageMi = new JMenuItem("Marine Management"),
                exportMi = new JMenuItem("Publish Log"),
                userMi = new JMenuItem("User Guide"),
                configMi = new JMenuItem("Configuration"),
                signMi = new JMenuItem("Duty Sign In");
        signMi.addActionListener(l -> signIn());
        adminMenu.add(signMi);        
        configMi.addActionListener(l -> configAction());
        adminMenu.add(configMi);
        manageMi.addActionListener(e -> new OptionFrame(ms));
        adminMenu.add(manageMi);
        adminMenu.addSeparator();
        exportMi.addActionListener(l -> exportLog());
        adminMenu.add(exportMi);
        jmb.add(adminMenu);
        userMi.addActionListener(l -> openGuide());
        helpMenu.add(userMi);
        jmb.add(helpMenu);
        this.setJMenuBar(jmb);
        // table setup
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth(tc.getHeaderValue().toString().length() * 10);
        }
        table.setRowSelectionAllowed(false);
        table.setDefaultRenderer(LogDateTime.class, new LogTimeRenderer());
        // add top panel and table to frame
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(bldTopPanel()), BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }
        });
    }

    // returns panel composed of lookup, list and check panels
    private JPanel bldTopPanel() {
        JPanel topPan = new JPanel();
        topPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        topPan.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        topPan.add(lkPan, c); // lookup panel
        c.gridy = 1;
        topPan.add(listPan, c); // list pane
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        topPan.add(chkPan, c); // check panel
        return topPan;
    }

    // GUI component actions
    // invoked with windows closing event
    private void close() {
        if (log.getRowCount() > 0) {
            int i = JOptionPane.showConfirmDialog(this, "Log Not Published, "
                    + "Continue With Exit?\nNote: Unpublished Entries Will Be "
                    + "Recovered On Startup", "Confirm Exit", 
                    JOptionPane.YES_NO_OPTION);
            if (i != 0) {
                return;
            }
        }
        this.dispose();
    }

    // alternative duty sign in interface
    // prompts user for sign in, true if marine signed in 
//    private boolean signIn() {
//        LookupPanel lp = new LookupPanel(ms);
//        String[] options = {"Sign In","Cancel"};
//        Marine m;
//        int i = JOptionPane.showOptionDialog(this, lp, "On-Duty Marine Sign In", 
//                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
//                null, options, null);
//        if (i == 1) {
//            return false;
//        } 
//        m = lp.getMarine();
//        if (m == null) {
//            i = conMessage(this, "Marine Record Not Found\n"
//                    + "Go To Add Marine?");
//            if (i == 0) {
//                new OptionFrame(ms);                
//            }
//            return false;
//        }
//        duty = m;
//        chkPan.updateDuty();
//        return true;
//    }
    
        // prompts user for sign in, true if marine signed in 
    private boolean signIn() {
        String[] options = {"Sign In","Cancel"};
        String str = JOptionPane.showInputDialog(this, "Enter DODID:", 
                "On-Duty Marine Sign In",JOptionPane.INFORMATION_MESSAGE);
        if (str == null) return false;
        Marine m;
        try {
            m = ms.lookup(Long.parseLong(str));
        } catch (NumberFormatException nfe){
            errMessage(this, "DODID Must Be Numeric Value\n" + nfe.getMessage());
            return signIn();
        }
        if (m == null) {
            int i = conMessage(this, "Marine Record Not Found For Entered DODID\n"
                    + "Go To Add Marine?");
            if (i == 0) {
                new OptionFrame(ms);                
            }
            return false;
        }
        duty = m;
        chkPan.updateDuty();
        return true;
    }

    // invoked with config menu item action
    private void configAction() {
        new ConfigFrame(ms);
    }

    // invoked with user guide menu item action, opens user guide
    private void openGuide() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop dt = Desktop.getDesktop();
                File f = new File("LeisureLogUsersGuide.htm");
                dt.browse(f.toURI());
            } else {
                errMessage(this, "Action Not Supported");
            }
        } catch (IOException ioe) {
            errMessage(this, "User Guide Not Found");
        }
    }

    // invoked with export menu item action, calls log to publish
    private void exportLog() {
        try {
            if (log.getOutCnt() > 0) {
                int i = conMessage(this, "Log Contains Open Entries, Continue?");
                if (i != 0) {
                    return;
                }
            }
            if (logDirectoryPath == null) {
                errMessage(this, "No Publish Directory Selected");
                new ConfigFrame(ms);
                return;
            }
            if (duty == null) {
                if (!signIn()) return;
            }
            File[] pubFiles = log.export(duty, logDirectoryPath);
            infoMessage(this, "Log files Created:\n " + Arrays.toString(pubFiles)
                    .replaceAll(",", "," + System.lineSeparator()));
            logBackup();
        } catch (IOException ioe) {
            errMessage(this, "Error Attempting To Export Log\n" + ioe.getMessage());
        } catch (NumberFormatException nfe) {
            errMessage(this, "Invalid Number Format\n" + nfe.getMessage());
        }
    }

    // backs up active log to file
    private void logBackup() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("log.ser"))) {
            oos.writeInt(LeisureGroup.getGrpCnt());
            oos.writeObject(log);
        } catch (IOException ioe) {
            errMessage(this, "I/O Error While Attempting Log Backup");
        }
    }

    // update config file used for initialize 
    public static boolean writeConfig() {
        try (FileWriter fw = new FileWriter(new File("log.ini"))) {
            fw.write("marine_data_file=" + marineFilePath);
            fw.write(System.lineSeparator());
            fw.write("log_directory=" + logDirectoryPath);
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    // setters for config files paths
    public static void setMarineFile(Path mfp) {
        marineFilePath = mfp;
    }

    public static void setLogDirectory(Path ld) {
        logDirectoryPath = ld;
    }

    //getters for config file paths
    public static Path getMarineFile() {
        return marineFilePath;
    }

    public static Path getLogDirectory() {
        return logDirectoryPath;
    }

    // opens file chooser, returns file if selected, null otherwise
    public static File chooseFile(Component c, String str, int selectMode,
            String path) {
        JFileChooser jfc = new JFileChooser(path);
        jfc.setFileSelectionMode(selectMode);
        if (jfc.showDialog(c, str)
                == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        } else {
            return null;
        }
    }

    // two argument file chooser
    public static File chooseFile(Component c, String str) {
        return chooseFile(c, str, JFileChooser.FILES_ONLY, ".");
    }

    // three argument file chooser
    public static File chooseFile(Component c, String message, String path) {
        return chooseFile(c, message, JFileChooser.FILES_ONLY, path);
    }

    // generic get input from user
    public static String inputMessage(Component c, String str) {
        return JOptionPane.showInputDialog(c, str);
    }

    // generic error messsage
    public static void errMessage(Component c, String str) {
        JOptionPane.showMessageDialog(c,
                str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //generic info message
    public static void infoMessage(Component c, String str) {
        JOptionPane.showMessageDialog(c, str, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // generic yes no cancel question, 0 yes, 1 no, 2 cancel
    public static int conMessage(Component c, String str) {
        return JOptionPane.showConfirmDialog(c, str);
    }

    // Panel builds list of Marines for check-out
    private class ListPanel extends JPanel {

        private static final long serialVersionUID = 3705684176899539313L;
        // Panel componenets
        private DefaultListModel<Marine> dlmGrp = new DefaultListModel<>();
        private JList<Marine> jlGrp = new JList<>(dlmGrp);
        private JButton addBtn = new JButton("Add To Group"),
                remBtn = new JButton("Remove");

        // constructor builds panel
        ListPanel() {
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(5, 20, 0, 0);
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.insets = i;
            c.anchor = GridBagConstraints.CENTER;
            c.gridy = 0;
            c.gridx = 0;
            addBtn.addActionListener(e -> add());
            this.add(addBtn, c);
            c.gridx = 1;
            i.set(5, 0, 0, 30);
            remBtn.addActionListener(e -> remove());
            this.add(remBtn, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridwidth = 2;
            c.gridy = 1;
            // list setup
            jlGrp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            jlGrp.setVisibleRowCount(4);
            // dummy marine for list prototype, fix list size
            jlGrp.setPrototypeCellValue(new Marine(1234567890, Marine.Grade.E3,
                    "Firstname", "M", "Lastname", 123, Marine.Tier.T1));
            JScrollPane jsp = new JScrollPane(jlGrp);
            i.set(5, 20, 5, 25);
            this.add(jsp, c);
        }

        //return array list of marines in group list
        private Marine[] getList() {
            Marine[] ma = new Marine[dlmGrp.size()];
            dlmGrp.copyInto(ma);
            return ma;
        }

        // invoked with add button action, gets Marine from lkPan, adds to list
        private void add() {
            Marine m = lkPan.getMarine();
            if (m == null) {
                errMessage(this, "No Marine on Display");
                return;
            }
            if (dlmGrp.contains(m)) {
                errMessage(this, "Marine Already in Group");
                return;
            }
            dlmGrp.addElement(m);
            lkPan.clear();
        }

        // clears list
        private void clear() {
            dlmGrp.clear();
        }

        // remove selected entry from list
        private void remove() {
            int[] selArr = jlGrp.getSelectedIndices();
            if (dlmGrp.isEmpty()) {
                errMessage(this, "List Currently Empty");
            } else if (selArr.length == 0) {
                errMessage(this, "No Selection Made");
            } else {
                dlmGrp.removeRange(selArr[0], selArr[selArr.length - 1]);
            }
        }
    }

    // Check in/out panel
    private class CheckPanel extends JPanel {

        private static final long serialVersionUID = 2286389252973980351L;
        private JButton chkInBtn = new JButton("Check In"),
                chkOutBtn = new JButton("Check Out");
        private JTextField jtfDest = new JTextField(),
                jtfContact = new JTextField();
        private JLabel chkLbl = new JLabel("<html><center>Leisure Log Start<br>"
                + new LogDateTime().toString() + "</html>", SwingConstants.CENTER),
                dutyLbl = new JLabel("On-Duty Marine: " + duty);

        CheckPanel() {
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(5, 5, 0, 0);
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.insets = i;
            c.gridx = 0;            
            c.gridwidth = 2;
            c.gridy = 0;            
            i.set(10, 0, 10, 10);
            // read, resize and add icon
            try {
                Image icon = ImageIO.read(new File("Marine.jpg"));
                ImageIcon ii
                        = new ImageIcon(icon.getScaledInstance(180, 100, Image.SCALE_SMOOTH));
                this.add(new JLabel(ii), c);
            } catch (IOException fnfe) { //dummy box if exception with image
                this.add(Box.createRigidArea(new Dimension(180, 100)));
            }
            i.set(5, 0, 0, 10);
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(dutyLbl, c);           
            c.gridy = 2;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            this.add(new JLabel("Enter Destination:"), c);
            c.gridx = 1;
            this.add(new JLabel("Enter Contact Number:"), c);
            c.gridx = 0;
            c.gridy = 3;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            i.set(0, 0, 5, 10);
            this.add(jtfDest, c);
            c.gridx = 1;
            this.add(jtfContact, c);
            c.gridx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.FIRST_LINE_END;
            i.set(5, 0, 5, 25);
            c.gridy = 4;
            chkOutBtn.addActionListener(e -> checkOut());
            this.add(chkOutBtn, c);
            c.gridy = 5;
            chkInBtn.setPreferredSize(chkOutBtn.getPreferredSize());
            c.anchor = GridBagConstraints.LAST_LINE_END;
            chkInBtn.addActionListener(e -> checkIn());
            this.add(chkInBtn, c);
            c.gridx = 1;
            c.gridy = 4;
            c.gridheight = 2;
            c.anchor = GridBagConstraints.CENTER;
            i.set(5, 3, 5, 10);
            Dimension d = chkInBtn.getPreferredSize();
            chkLbl.setPreferredSize(new Dimension((int) (d.getWidth() * 1.5),
                    (int) (d.getHeight() * 1.5)));
            chkLbl.setMinimumSize(chkLbl.getPreferredSize());
            chkLbl.setMaximumSize(chkLbl.getPreferredSize());
            chkLbl.setOpaque(true);
            chkLbl.setBackground(Color.GREEN.darker());
            chkLbl.setForeground(Color.WHITE);
            this.add(chkLbl, c);
            jtfDest.setPreferredSize(new Dimension((int) (d.getWidth() * 1.5),
                    jtfDest.getPreferredSize().height));

        }
        
        // updates duty label after Marine sign in
        private void updateDuty(){
            dutyLbl.setText("On-Duty Marine: " + duty);
        }

        // gets marines, time and destination, calls log checkout
        private void checkOut() {
            Marine[] marArr = listPan.getList();
            if (marArr.length == 0) {
                chkLbl.setText("Check Out Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Marines In Group");
                return;
            }
            String dest = jtfDest.getText().trim();
            if (dest.isEmpty()) {
                chkLbl.setText("Check Out Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Destination Entered");
                return;
            }
            String contact = jtfContact.getText().trim();
            if (contact.isEmpty()) {
                chkLbl.setText("Check Out Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Contact Number Entered");
                return;
            }
            LogDateTime ldt;
            try {
                ldt = log.chkOut(marArr, dest, contact);
            } catch (CheckoutException ce) {
                chkLbl.setText("Check Out Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, ce.getMessage() + "\n" + ce.getMarine());
                return;
            }
            listPan.clear();
            jtfDest.setText("");
            jtfContact.setText("");
            chkLbl.setBackground(Color.GREEN.darker());
            chkLbl.setText("<html><center>Check Out Successfull<br>"
                    + ldt.toString() + "</html>");
            logBackup();
        }

        // calls log to check in selected  
        private void checkIn() {
            LogDateTime ldt = log.chkIn();
            if (ldt != null){
                chkLbl.setBackground(Color.GREEN.darker());
                chkLbl.setText("<html><center>Check In Successfull<br>"
                    + ldt.toString() + "</html>");
                logBackup();
            } else {
                chkLbl.setText("Check In Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Marines Selected");                
            }
        }
    }

    // table cell renderer for LogDateTime Class display
    private class LogTimeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 7060424854144269811L;
        
        // returns component used for cell render
        @Override
        public Component getTableCellRendererComponent(JTable table, Object Value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, Value,
                    isSelected, hasFocus, row, col);
            JLabel label = (JLabel) c;
            // component background depends on late check flag status
            if (log.hasFlag(row)) {
                label.setBackground(Color.red);
            } else {
                label.setBackground(table.getBackground());
            }
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return label;
        }
    }

}
