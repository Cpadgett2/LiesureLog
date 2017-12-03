package leisurelog;

import java.awt.*;
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
    private static Path logDirectoryPath, marineFilePath;

    //constructor
    LeisureLog() throws IOException {
        super("Leisure Log");
        this.setSize(750, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initilize();
        bldGUI();
        this.setVisible(true);
        //new ConfigFrame();
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
            errMessage(this, "Problem Reading Initialization Files\n"
                    + e.getMessage());
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
            //System.out.println(exc);
            return new Log();
        } catch (ClassNotFoundException cnf) {
            //System.out.println("class not found");
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
                configMi = new JMenuItem("Configuration");
        manageMi.addActionListener(e -> new OptionFrame(ms));
        adminMenu.add(manageMi);
        configMi.addActionListener(l -> configAction());
        adminMenu.add(configMi);
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
        topPan.add(lkPan, c); // lookup pane
        c.gridy = 1;
        topPan.add(listPan, c); // list pane
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        topPan.add(chkPan, c); // check panel
        return topPan;
    }

    // GUI component actions
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
            Marine duty = ms.lookup(Long.parseLong(inputMessage(this,
                    "Enter DODID of On-Duty Marine")));
            if (duty == null) {
                int i = conMessage(this, "Marine Not Found For Entered DODID\n"
                        + "Go To Add Marine?");
                if (i == 0) {
                    new OptionFrame(ms);
                }
                return;
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
    public static Path getMarineFile(){
        return marineFilePath;
    }
    
    public static Path getLogDirectory(){
        return logDirectoryPath;
    }

    // opens file chooser, returns file if selected, null otherwise
    public static File chooseFile(Component c, String str, int selectMode) {
        JFileChooser jfc = new JFileChooser(".");
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
        return chooseFile(c, str, JFileChooser.FILES_ONLY);
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
            //dlmGrp.addElement(new Marine());
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
                + new LogDateTime().toString() + "</html>", SwingConstants.CENTER);

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
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            this.add(new JLabel("Enter Destination:"), c);
            c.gridx = 1;
            this.add(new JLabel("Enter Contact Number:"), c);
            c.gridx = 0;
            c.gridy = 2;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            i.set(0, 0, 5, 10);
            this.add(jtfDest, c);
            c.gridx = 1;
            this.add(jtfContact, c);
            c.gridx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.FIRST_LINE_END;
            i.set(5, 0, 5, 25);
            c.gridy = 3;
            chkOutBtn.addActionListener(e -> checkOut());
            this.add(chkOutBtn, c);
            c.gridy = 4;
            chkInBtn.setPreferredSize(chkOutBtn.getPreferredSize());
            c.anchor = GridBagConstraints.LAST_LINE_END;
            chkInBtn.addActionListener(e -> checkIn());
            this.add(chkInBtn, c);
            c.gridx = 1;
            c.gridy = 3;
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
            LogDateTime ldt = new LogDateTime();
            chkLbl.setText("<html><center>Check In Successfull<br>"
                    + ldt.toString() + "</html>");
            log.chkIn();
            logBackup();
        }
    }

    private class LogTimeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object Value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, Value,
                    isSelected, hasFocus, row, col);
            JLabel label = (JLabel) c;
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
