package leisurelog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class LeisureLog extends JFrame {

    private MarinePanel mp = new MarinePanel();
    private ListPanel lp = new ListPanel();
    private CheckPanel cp = new CheckPanel();
    private String[] col = {" ", "Group", "  DODID  ", "     Marine     ",
        "Destination", "Check-Out", "Check-In"};
    private DefaultTableModel dtm = new DefaultTableModel(col, 1);
    private JTable table = new JTable(dtm);
    private JMenuItem addMi = new JMenuItem("Marine Options");

    LeisureLog() {
        super("Leisure Log");
        this.setSize(650, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar jmb = new JMenuBar();
        JMenu jm = new JMenu("Admin");
        addMi.addActionListener(e -> options());
        jm.add(addMi);
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
        //this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        //new JScrollPane(mp),
        //new JScrollPane(new JTable(dtm))));
        this.setVisible(true);
    }

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

    private void options() {
        new OptionFrame();
    }

    // generic error messsage
    private void errMessage(String str) {
        JOptionPane.showMessageDialog(this,
                str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new LeisureLog();
    }

    // Marine lookup panel
    private class MarinePanel extends JPanel {

        private JTextField jtfID = new JTextField(10);
        private JButton lkbtn = new JButton("Lookup");
        private JLabel nameLbl = new JLabel("Roosevelt, Theodore"),
                rankLbl = new JLabel("Rank"),
                rmLbl = new JLabel("303"),
                tierLbl = new JLabel("T2");

        MarinePanel() {
            //this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            this.setLayout(new GridBagLayout());
            nameLbl.setFont(nameLbl.getFont().deriveFont(Font.PLAIN));
            rankLbl.setFont(nameLbl.getFont());
            rmLbl.setFont(nameLbl.getFont());
            tierLbl.setFont(nameLbl.getFont());
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(5, 10, 0, 0);
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.insets = i;
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            this.add(new JLabel("Enter DODID:"), c);
            c.gridx = 1;
            this.add(Box.createHorizontalStrut(50), c);
            c.gridx = 0;
            c.gridwidth = 2;
            c.gridy = 1;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            i.set(0, 10, 5, 0);
            this.add(jtfID, c);
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.NONE;
            c.gridx = 2;
            c.gridwidth = 1;
            c.gridy = 0;
            c.gridheight = 2;
            i.set(5, 3, 0, 10);
            lkbtn.addActionListener(e -> lookup());
            this.add(lkbtn, c);
            c.gridx = 0;
            c.gridy = 2;
            c.gridheight = 1;
            c.anchor = GridBagConstraints.LINE_END;
            i.set(0, 5, 3, 1);
            this.add(new JLabel("Name: "), c);
            c.gridy = 3;
            this.add(new JLabel("Rank: "), c);
            c.gridy = 4;
            this.add(new JLabel("Room: "), c);
            c.gridy = 5;
            this.add(new JLabel("Tier: "), c);
            i.set(0, 1, 3, 10);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 1;
            c.gridwidth = 2;
            c.gridy = 2;
            this.add(nameLbl, c);
            c.gridy = 3;
            this.add(rankLbl, c);
            c.gridy = 4;
            this.add(rmLbl, c);
            c.gridy = 5;
            this.add(tierLbl, c);
        }

        // get Marine from structure based on DODID entered
        private void lookup() {
        }

        //return marine currently on display in panel
        //private Marine getMarine(){}    
    }

    // Marine group list panel
    private class ListPanel extends JPanel {

        private DefaultListModel<String> dlmGrp = new DefaultListModel<>();
        private JList<String> jlGrp = new JList<>(dlmGrp);
        private JButton addBtn = new JButton("Add To Group"),
                remBtn = new JButton("Remove");

        ListPanel() {
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(5, 10, 0, 0);
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
            //remBtn.setPreferredSize(addBtn.getPreferredSize());
            remBtn.addActionListener(e -> remove());
            this.add(remBtn, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridwidth = 2;
            c.gridy = 1;
            dlmGrp.addElement("LCpl Smith1, John H 242 T2");
            dlmGrp.addElement("LCpl Smith2, John H 242 T2");
            dlmGrp.addElement("LCpl Smith3, John H 242 T2");
            jlGrp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            jlGrp.setVisibleRowCount(4);
            JScrollPane jsp = new JScrollPane(jlGrp);
            i.set(5, 10, 5, 30);
            this.add(jsp, c);
        }

        //return array list of marines in group list
        //private ArrayList<Marine> getList(){}
        // add marine from marine panel to list
        private void add() {
        }

        // remove selected entry from list
        private void remove() {
            int[] selArr = jlGrp.getSelectedIndices();
            if (dlmGrp.isEmpty()) {
                errMessage("List Currently Empty");
            } else if (selArr.length == 0) {
                errMessage("No Selection Made");
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
        private JLabel chkInLbl = new JLabel("Time In"),
                chkOutLbl = new JLabel("Time Out");

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
            c.anchor = GridBagConstraints.LAST_LINE_START;
            i.set(5, 3, 5, 10);
            this.add(chkInLbl, c);
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridy = 3;
            this.add(chkOutLbl, c);
        }

        private void checkOut() {
        }

        private void checkIn() {
        }
    }

    private class OptionFrame extends JFrame {

        private JTabbedPane jtp = new JTabbedPane();
        private JButton remBtn = new JButton("Remove Marine");
        
        OptionFrame() {
            super("Marine Options");
            this.setSize(300, 300);
            this.setLocationRelativeTo(mp);
            this.setAlwaysOnTop(true);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLayout(new GridLayout(1,1));
            
            jtp.add("Remove", bldRemPan());
            this.add(jtp);
            this.setVisible(true);
        }
        
        private JPanel bldRemPan(){
            JPanel remPan = new JPanel();
            remPan.setLayout(new BorderLayout());
            remPan.add(new MarinePanel(),BorderLayout.CENTER);
            remPan.add(remBtn, BorderLayout.SOUTH);
            return remPan;
        }
    }

}
