package leisurelog;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;

public class LeisureLog extends JFrame {

    // log table
    private Log log = new Log();
    private JTable table = new JTable(log);
    private JMenuItem addMi = new JMenuItem("Marine Options"),
            exportMi = new JMenuItem("Export Log");
    //inner top pannels 
    private MarinePanel mp = new MarinePanel();
    private ListPanel lp = new ListPanel();
    private CheckPanel cp = new CheckPanel();
    //marine stucture
    //private MarineStructure ms;

    //constructor
    LeisureLog() {
        super("Leisure Log");
        this.setSize(730, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar jmb = new JMenuBar();
        JMenu jm = new JMenu("Admin");
        addMi.addActionListener(e -> new OptionFrame());
        jm.add(addMi);
        //jm.addSeparator();
        jm.add(exportMi);
        jmb.add(jm);
        this.setJMenuBar(jmb);
        this.setLayout(new BorderLayout());
        //this.add(mp, BorderLayout.NORTH);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth(tc.getHeaderValue().toString().length() * 10);
        }
        this.add(new JScrollPane(bldTopPan()), BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        table.setRowSelectionAllowed(false);
        //this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        //new JScrollPane(mp),
        //new JScrollPane(new JTable(dtm))));
        //this.setVisible(true);

    }

    public static void main(String[] args) {
        LeisureLog ll = new LeisureLog();
        ll.initilize();
    }

    // program initialization 
    private void initilize() {
        try (Scanner sc = new Scanner(new File("config"))) {
            sc.useDelimiter("=");
            String fileName = "";
            while (sc.hasNextLine()) {
                if (sc.next().equalsIgnoreCase("marine_data_file")) {
                    if (sc.hasNext()) {
                        fileName = sc.next();
                    }
                }
            }
            File mdf = new File(fileName);
            System.out.println(mdf);
            if (!mdf.canRead()) throw new FileNotFoundException();
            //ms = new MarineStructure(new File(dataFile));
        } catch (FileNotFoundException | NullPointerException e) {
            File f = chooseFile(this, "Select Marine Data File");
            if (f != null) {
                //ms = new MarineStructure(jfc.getSelectedFile());
                writeConfig(f);
                System.out.println(f);
            } else {
                int i = conMessage(this, "No Marine Data File Selected\n"
                        + "Go To Marine Add Window?");
                if (i == 0) {
                    new OptionFrame();
                }
            }
        }
        this.setVisible(true);
    }
    
    // rewrite config file used for initialize 
    private void writeConfig(File marineFile){
        try(FileWriter fw = new FileWriter(new File("config"))){
            fw.write("marine_data_file=" + marineFile.toString());
        } catch (IOException ioe){}
    }

    // constructs top panel consisting of marine panel, list panel, check panel
    private JPanel bldTopPan() {
        JPanel topPan = new JPanel();
        topPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        topPan.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        topPan.add(mp, c);
        c.gridy = 1;
        topPan.add(lp, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        cp.setPreferredSize(new Dimension(mp.getPreferredSize().width,
                cp.getPreferredSize().height));
        topPan.add(cp, c);
        return topPan;
    }

    // opens file chooser, returns file if selected, null otherwise
    private File chooseFile(Component c, String str) {
        JFileChooser jfc = new JFileChooser(".");
        if (jfc.showDialog(c, str)
                == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        } else {
            return null;
        }
    }

    // generic error messsage
    private void errMessage(Component c, String str) {
        JOptionPane.showMessageDialog(c,
                str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // yes no cancel question
    private int conMessage(Component c, String str) {
        return JOptionPane.showConfirmDialog(c, str);//, "", 
        //JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    // Marine lookup panel
    private class MarinePanel extends JPanel {

        private JTextField jtfID = new JTextField(10);
        private JButton lkBtn = new JButton("Lookup"),
                clrBtn = new JButton("Clear");
        private JLabel nameLbl = new JLabel("Roosevelt, Theodore"),
                rankLbl = new JLabel("Rank"),
                rmLbl = new JLabel("303"),
                tierLbl = new JLabel("T2"),
                idLbl = new JLabel("1234567890"),
                grLbl = new JLabel("E3");

        MarinePanel() {
            this.setLayout(new GridBagLayout());
            Font f = nameLbl.getFont().deriveFont(Font.PLAIN);
            nameLbl.setFont(f);
            rankLbl.setFont(f);
            rmLbl.setFont(f);
            tierLbl.setFont(f);
            idLbl.setFont(f);
            grLbl.setFont(f);
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(10, 15, 0, 0);
            c.weightx = 0.9;
            c.weighty = 0.5;
            c.insets = i;
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            this.add(new JLabel("Enter DODID:"), c);
            c.weightx = 0.1;
            c.gridx = 1;
            this.add(Box.createHorizontalStrut(30), c);
            c.gridx = 0;
            c.weightx = 0.9;
            c.gridwidth = 2;
            c.gridy = 1;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            i.set(0, 15, 5, 0);
            this.add(jtfID, c);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 2;
            c.weightx = 0.1;
            c.gridwidth = 2;
            c.gridy = 0;
            c.gridheight = 2;
            i.set(5, 3, 0, 1);
            c.fill = GridBagConstraints.NONE;
            lkBtn.addActionListener(e -> lookup());
            this.add(lkBtn, c);
            c.gridwidth = 1;
            c.gridx = 4;
            clrBtn.setPreferredSize(lkBtn.getPreferredSize());
            i.set(5, 0, 0, 20);
            clrBtn.addActionListener(l -> clear());
            this.add(clrBtn, c);
            c.gridx = 0;
            c.weightx = 0.9;
            c.gridy = 2;
            c.gridheight = 1;
            c.anchor = GridBagConstraints.LINE_END;
            i.set(0, 5, 3, 1);
            this.add(new JLabel("ID: "), c);
            c.gridy = 3;
            this.add(new JLabel("Name: "), c);
            c.gridy = 4;
            this.add(new JLabel("Rank: "), c);
            c.gridx = 2;
            c.weightx = 0.1;
            this.add(new JLabel("Grade: "), c);
            c.gridx = 0;
            c.weightx = 0.9;
            c.gridy = 5;
            this.add(new JLabel("Room: "), c);
            c.gridy = 6;
            i.set(0, 5, 10, 1);
            this.add(new JLabel("Tier: "), c);
            i.set(0, 1, 3, 10);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 1;
            c.weightx = 0.1;
            c.gridwidth = 3;
            c.gridy = 2;
            this.add(idLbl, c);
            c.gridy = 3;
            this.add(nameLbl, c);
            c.gridwidth = 1;
            c.gridy = 4;
            this.add(rankLbl, c);
            c.gridx = 3;
            this.add(grLbl, c);
            c.gridx = 1;
            c.gridy = 5;
            this.add(rmLbl, c);
            c.gridy = 6;
            i.set(0, 1, 10, 10);
            this.add(tierLbl, c);
        }

        // get Marine from structure based on DODID entered
        private void lookup() {
            //log.add();
        }

        // clears panel labels
        private void clear() {
            nameLbl.setText(" ");
            rankLbl.setText(" ");
            rmLbl.setText(" ");
            tierLbl.setText(" ");
            idLbl.setText(" ");
            grLbl.setText(" ");
        }
        //return marine currently on display in panel
        //private Marine getMarine(){}    
    }

    // Marine group list panel
    private class ListPanel extends JPanel {

        private DefaultListModel<Marine> dlmGrp = new DefaultListModel<>();
        private JList<Marine> jlGrp = new JList<>(dlmGrp);
        private JButton addBtn = new JButton("Add To Group"),
                remBtn = new JButton("Remove");

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

            jlGrp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            jlGrp.setVisibleRowCount(4);
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

        //add marine from marine panel to list
        private void add() {
            dlmGrp.addElement(new Marine());
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

        private JButton chkInBtn = new JButton("Check In"),
                chkOutBtn = new JButton("Check Out");
        private JTextField jtfDest = new JTextField();
        private JLabel chkLbl = new JLabel("<html><center>Leisure Log Start<br>" + new LogDateTime().toString() + "</html>", SwingConstants.CENTER);

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
            } catch (IOException fnfe) {
                this.add(Box.createRigidArea(new Dimension(180, 100)));
            }
            i.set(5, 0, 0, 10);
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            this.add(new JLabel("Enter Destination:"), c);
            c.gridy = 2;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            i.set(0, 0, 5, 10);
            this.add(jtfDest, c);
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.FIRST_LINE_END;
            i.set(5, 0, 5, 3);
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
        }

        // gets group, time and destination, calls log checkout
        private void checkOut() {
            //LogDateTime ldt = new LogDateTime();
            Marine[] marArr = lp.getList();
            if (marArr.length == 0) {
               chkLbl.setText("Check Out Failure");
               //chkLbl.setText("<html><center>Check Out Failure<br>");
                        //+ ldt.toString() + "</html>");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Marines In Group");
                return;
            }
            String dest = jtfDest.getText().trim();
            if (dest.isEmpty()) {
                //chkLbl.setText("<html><center>Check Out Failure<br>"
                        //+ ldt.toString() + "</html>");
                chkLbl.setText("Check Out Failure");
                chkLbl.setBackground(Color.RED);
                errMessage(this, "No Destination Entered");
                return;
            }
            LogDateTime ldt = log.chkOut(
                    new LeisureGroup(marArr, dest, new LogDateTime()));
            lp.clear();
            jtfDest.setText("");            
            chkLbl.setBackground(Color.GREEN.darker());
            chkLbl.setText("<html><center>Check Out Successfull<br>"
                    + ldt.toString() + "</html>");

        }

        // calls log to check in selected  
        private void checkIn() {
            LogDateTime ldt = new LogDateTime();
            chkLbl.setText("<html><center>Check In Successfull<br>"
                    + ldt.toString() + "</html>");
            log.chkIn();
        }
    }

    // Marine options frame
    private class OptionFrame extends JFrame {

        // flag for structure change
        private boolean updated = false;
        private JTabbedPane jtp = new JTabbedPane();

        OptionFrame() {
            super("Marine Options");
            this.setSize(325, 270);
            this.setLocationRelativeTo(mp);
            this.setAlwaysOnTop(true);
            //this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.setLayout(new GridLayout(1, 1));
            //this.setLayout(new FlowLayout());
            jtp.add("Add Marine", new AddPanel());
            jtp.add("Update Marine", new UpdatePanel());
            jtp.add("Delete Marine", new RemovePanel());
            //jtp.add("test", remBtn);
            this.add(jtp);
            this.setResizable(false);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    close();
                }
            });
            this.setVisible(true);
            //jtp.addChangeListener(l -> tabChange());
        }

        // close frame options
        private void close() {
            if (updated) {
                int i = conMessage(this, "Save Changes To Marine File?");
                if (i == 2) {
                    return; //cancel
                }
                if (i == 0) { // yes save changes
                    File f = chooseFile(this,"Save Marine Data File");
                    if (f == null) return;
                    writeConfig(f);
                }
                System.out.println("no save");
            }
            this.dispose();
        }

        // Remove Marine panel 
        private class RemovePanel extends JPanel {

            private JButton remBtn = new JButton("Delete Marine");
            private MarinePanel mpOp = new MarinePanel();

            RemovePanel() {
                this.setLayout(new BorderLayout());
                this.add(mpOp, BorderLayout.CENTER);
                this.add(remBtn, BorderLayout.SOUTH);
                remBtn.addActionListener(e -> remove());
            }

            // remove button action
            private void remove() {
                if (!updated) {
                    updated = true;
                }
                System.out.println("remove marine");
            }

        }

        // Add new Marine panel
        private class AddPanel extends JPanel {

            private JButton addBtn = new JButton("Add Marine");
            private InfoPanel addInfoPan = new InfoPanel(false);

            AddPanel() {
                this.setLayout(new BorderLayout());
                this.add(addInfoPan, BorderLayout.CENTER);
                this.add(addBtn, BorderLayout.SOUTH);
                addBtn.addActionListener(e -> add());
            }

            //add button action
            private void add() {
                if (!updated) {
                    updated = true;
                }
                System.out.println("add marine");
            }
        }

        // Update Existing Marine panel
        private class UpdatePanel extends JPanel {

            private JButton upBtn = new JButton("Update Marine");
            private InfoPanel upInfoPan = new InfoPanel(true);

            UpdatePanel() {
                this.setLayout(new BorderLayout());
                this.add(upInfoPan, BorderLayout.CENTER);
                this.add(upBtn, BorderLayout.SOUTH);
                upBtn.addActionListener(e -> update());
            }

            // update button action
            private void update() {
                if (!updated) {
                    updated = true;
                }
                System.out.println("update marine");
            }

        }

        // Panel for Marine info collection, used in both add and update panels
        private class InfoPanel extends JPanel {

            private JTextField dodTxt = new JTextField(10),
                    firstTxt = new JTextField(10),
                    midTxt = new JTextField(1),
                    lastTxt = new JTextField(10),
                    //rankTxt = new JTextField(5),
                    roomTxt = new JTextField(3);
            private final String[] tierStr = {"T1", "T2", "T3"};
            private final String[] rankStr = {"Pvt", "PFC", "LCpl", "Cpl", "Sgt"};
            private JComboBox tierCmb = new JComboBox(tierStr),
                    rankCmb = new JComboBox(rankStr);
            private JButton popBtn = new JButton("Populate");
            private JLabel grLbl = new JLabel("E1");

            InfoPanel(boolean pop) {
                this.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                Insets i = new Insets(2, 5, 2, 5);
                c.weightx = 0.5;
                c.weighty = 0.5;
                c.insets = i;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 3;
                this.add(new JLabel("DODID: "), c);
                c.gridy = 1;
                this.add(dodTxt, c);
                c.gridy = 2;
                c.anchor = GridBagConstraints.PAGE_END;
                this.add(new JLabel("Name: "), c);
                c.gridwidth = 2;
                c.gridy = 3;
                this.add(firstTxt, c);
                c.gridwidth = 1;
                c.gridx = 2;
                this.add(midTxt, c);
                c.gridx = 3;
                this.add(lastTxt, c);
                c.gridx = 0;
                c.gridy = 4;
                c.anchor = GridBagConstraints.PAGE_START;
                JLabel f = new JLabel("First");
                Font font = f.getFont().deriveFont(Font.PLAIN, f.getFont().getSize() - 2);
                f.setFont(font);
                this.add(f, c);
                c.gridx = 2;
                JLabel mi = new JLabel("MI");
                mi.setFont(font);
                this.add(mi, c);
                c.gridx = 3;
                JLabel last = new JLabel("Last");
                last.setFont(font);
                this.add(last, c);
                c.gridx = 0;
                c.gridwidth = 2;
                c.gridy = 5;
                c.anchor = GridBagConstraints.PAGE_END;
                this.add(new JLabel("Additional Info:"), c);
                c.gridwidth = 1;
                c.gridy = 6;
                rankCmb.addActionListener(l -> grUpdate());
                this.add(rankCmb, c);
                c.gridx = 1;
                grLbl.setBorder(BorderFactory.createEtchedBorder());
                this.add(grLbl, c);
                c.gridx = 2;
                this.add(roomTxt, c);
                c.gridx = 3;
                this.add(tierCmb, c);
                c.gridx = 0;
                c.gridy = 7;
                c.anchor = GridBagConstraints.PAGE_START;
                JLabel rank = new JLabel("Rank");
                rank.setFont(font);
                this.add(rank, c);
                c.gridx = 1;
                JLabel grade = new JLabel("Grade");
                grade.setFont(font);
                this.add(grade, c);
                c.gridx = 2;
                JLabel room = new JLabel("Room");
                room.setFont(font);
                this.add(room, c);
                c.gridx = 3;
                JLabel tier = new JLabel("Tier Level");
                tier.setFont(font);
                this.add(tier, c);
                if (pop) {
                    c.anchor = GridBagConstraints.CENTER;
                    c.gridx = 3;
                    c.gridy = 0;
                    c.gridwidth = 1;
                    c.gridheight = 2;
                    this.add(popBtn, c);
                    popBtn.addActionListener(e -> populate());
                }
            }

            // Update grade for rank selection
            private void grUpdate() {
                grLbl.setText("E" + (rankCmb.getSelectedIndex() + 1));
            }

            // populates fields with existing Marine info
            private void populate() {
                System.out.println("populate");
            }
        }
    }

}
