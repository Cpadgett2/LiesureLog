/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leisurelog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

public class LeisureLog extends JFrame {

    MarinePanel mp = new MarinePanel();
    DefaultTableModel dtm = new DefaultTableModel(10,7);
    JMenuItem addMi = new JMenuItem("Add Marine");
    
    LeisureLog() {
        super("Leisure Log");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar jmb = new JMenuBar();
        JMenu jm = new JMenu("Admin");
        jm.add(addMi);
        jmb.add(jm);
        this.setJMenuBar(jmb);
        this.setLayout(new BorderLayout());
        this.add(mp, BorderLayout.NORTH);
        this.add(new JScrollPane(new JTable(dtm)), BorderLayout.CENTER);
        //this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                //new JScrollPane(mp),
                //new JScrollPane(new JTable(dtm))));
        this.setVisible(true);

    }

    public static void main(String[] args) {
        new LeisureLog();
    }

    private static class MarinePanel extends JPanel {

        private JTextField jtfID = new JTextField(10),
                jtfDest = new JTextField();        
        private JButton btn = new JButton("Lookup"),
                addBtn = new JButton("Add To Group"),
                remBtn = new JButton("Clear"),
                chkInBtn = new JButton("Check In"),
                chkOutBtn = new JButton("Check Out");
        private JLabel nameLbl = new JLabel("Roosevelt, Theodore"),
                rankLbl = new JLabel("Rank"),
                rmLbl = new JLabel("303"),
                tierLbl = new JLabel("T2"),
                chkInLbl = new JLabel("Time In"),
                chkOutLbl = new JLabel("Time Out");
        private DefaultListModel<String> dlmGrp = new DefaultListModel<>();
        private JList<String> jlGrp = new JList<>(dlmGrp);
        

        MarinePanel() {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            this.setLayout(new GridBagLayout());
            nameLbl.setFont(nameLbl.getFont().deriveFont(Font.PLAIN));
            rankLbl.setFont(nameLbl.getFont());
            rmLbl.setFont(nameLbl.getFont());
            tierLbl.setFont(nameLbl.getFont());
            GridBagConstraints c = new GridBagConstraints();
            Insets i = new Insets(5, 5, 0, 0);
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.insets = i;
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.LAST_LINE_START;
           // c.gridwidth = 2;
            //JLabel idLbl = new JLabel("Enter DODID:");
            //idLbl.setFont(new Font("Calibri Light", Font.BOLD, idLbl.getFont().getSize()+2));
            this.add(new JLabel("Enter DODID:"), c);
            c.gridx = 1;
            //c.gridwidth=1;
            this.add(Box.createHorizontalStrut(50), c);
            c.gridx = 0;            
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridy = 1;
            c.gridwidth = 2;
            i.set(0, 5, 3, 0);
            this.add(jtfID, c);
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            c.gridx = 2;
            c.gridy = 0;
            c.gridwidth = 1;
            c.gridheight = 2;
            i.set(5, 0, 0, 10);
            this.add(btn, c);
            c.gridheight = 1;
            c.gridx = 0;
            c.gridy = 2;
            c.anchor = GridBagConstraints.LINE_END;
            i.set(0, 1, 2, 1);
            this.add(new JLabel("Name: "), c);
            c.gridy = 3;
            this.add(new JLabel("Rank: "), c);
            c.gridy = 4;
            this.add(new JLabel("Room: "), c);
            c.gridy = 5;
            this.add(new JLabel("Tier: "), c);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridy = 2;
            c.gridx = 1;
            c.gridwidth = 2;
            this.add(nameLbl, c);
            c.gridy = 3;
            this.add(rankLbl, c);
            c.gridy = 4;
            this.add(rmLbl, c);
            c.gridy = 5;
            this.add(tierLbl, c);
            c.gridx = 3;
            //JLabel desLbl = new JLabel("Enter Destination:");
            //desLbl.setFont(new Font("Calibri Light", Font.BOLD, desLbl.getFont().getSize()+2));
            this.add(new JLabel("Enter Destination:"),c);
            c.anchor = GridBagConstraints.CENTER;
            c.gridy = 6;
            c.gridx = 0;
            
            this.add(addBtn, c);
            c.gridwidth = 1;
            c.gridx = 2;
            i.set(0, 0, 3, 10);
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(remBtn, c);
            c.gridx = 3;
            c.gridwidth=2;
            this.add(jtfDest,c);
            c.gridwidth = 3;
            c.gridx = 0;
            c.gridheight = 2;
            dlmGrp.addElement("LCpl Smith, John H 242 T2");
            jlGrp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jlGrp.setVisibleRowCount(4);
            JScrollPane jsp = new JScrollPane(jlGrp);
            c.gridy = 7;
            i.set(5, 10, 5, 10);
            this.add(jsp, c);            
            c.gridx=3;
            c.gridwidth=1;
            c.gridheight=1;
            c.fill=GridBagConstraints.NONE;
            c.anchor=GridBagConstraints.LINE_END;
            i.set(0,3,0,3);
            this.add(chkOutBtn,c);
            c.gridy=8;
            chkInBtn.setPreferredSize(chkOutBtn.getPreferredSize());
            this.add(chkInBtn,c);
            c.gridx=4;
            c.anchor=GridBagConstraints.LINE_START;
            this.add(chkInLbl,c);
            c.gridy=7;
            this.add(chkOutLbl,c);
            c.gridx=3;
            c.gridwidth=2;
            c.gridy=0;
            c.gridheight=5;
            c.fill=GridBagConstraints.NONE;
            c.anchor=GridBagConstraints.CENTER;
            i.set(10,30,10,30);
            try {
                Image icon = ImageIO.read(new File("Marine.jpg"));
                ImageIcon ii = 
                        new ImageIcon(icon.getScaledInstance(180, 100, Image.SCALE_SMOOTH));
                this.add(new JLabel(ii),c);
            } catch (IOException fnfe) {
                
            }
            
        }

    }

}
