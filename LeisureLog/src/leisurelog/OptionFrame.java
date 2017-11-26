package leisurelog;

// Marine options frame
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

class OptionFrame extends JFrame {

    // flag for structure change
    private boolean updated = false;
    private JTabbedPane jtp = new JTabbedPane();
    private final MarineStructure ms;

    OptionFrame() {
        this(new MarineStructure());
    }

    // builds frame with add, update and remove panels
    OptionFrame(MarineStructure ms) {
        super("Marine Options");
        this.ms = ms;
        this.setSize(325, 270);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new GridLayout(1, 1));
        // add panels to tabs
        jtp.add("Add Marine", new AddPanel());
        jtp.add("Update Marine", new UpdatePanel());
        jtp.add("Delete Marine", new RemovePanel());
        this.add(jtp);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }
        });
        this.setVisible(true);
    }

    // invoked when window closing, prompts user for save options
    private void close() {
        if (updated) {
            int i = LeisureLog.conMessage(this, "Save Changes To Marine File?");
            if (i == 2) {
                return; //cancel
            }
            if (i == 0) { // yes save changes
                File f = LeisureLog.chooseFile(this, "Save Marine Data File");
                if (f == null) {
                    return;
                }
                LeisureLog.writeConfig(f);
            }
            //System.out.println("no save");
        }
        this.dispose();
    }

    // Remove Marine panel 
    private class RemovePanel extends JPanel {

        private JButton remBtn = new JButton("Delete Marine");
        private LookupPanel lkPan = new LookupPanel(ms);

        RemovePanel() {
            this.setLayout(new BorderLayout());
            this.add(lkPan, BorderLayout.CENTER);
            this.add(remBtn, BorderLayout.SOUTH);
            remBtn.addActionListener(e -> remove());
        }

        // invoked with remove button action
        private void remove() {
            Marine m = lkPan.getMarine();
            if (m == null) {
                LeisureLog.errMessage(this, "No Marine Selected");
                return;
            }
            if (!ms.remove(m)) {
                LeisureLog.errMessage(this, "Error Removing, Marine Not Found");
                return;
            }
            if (!updated) {
                updated = true;
            }
            LeisureLog.infoMessage(this, "Successfully Removed Marine \n" + m);
            lkPan.clear();
            //System.out.println("remove marine");
        }

    }

    // Add new Marine panel
    private class AddPanel extends JPanel {

        private JButton addBtn = new JButton("Add Marine");
        private InfoPanel infoPan = new InfoPanel(false);

        AddPanel() {
            this.setLayout(new BorderLayout());
            this.add(infoPan, BorderLayout.CENTER);
            this.add(addBtn, BorderLayout.SOUTH);
            addBtn.addActionListener(e -> add());
        }

        // invoked with add button action, creates new marine adds to structure
        private void add() {
            try {
                Marine m = new Marine(infoPan.getDODID(), infoPan.getGrade(),
                        infoPan.getFirst(), infoPan.getMid(), infoPan.getLast(),
                        infoPan.getRoom(), infoPan.getTier());
                if (!ms.add(m)) {
                    LeisureLog.errMessage(this, "Error - Marine ID Already Present");
                    return;
                } else {
                    LeisureLog.infoMessage(this, "Successfully Added Marine\n " + m);
                    if (!updated) {
                        updated = true;
                    }
                    infoPan.clear();
                }
            } catch (RuntimeException rte) {
                LeisureLog.errMessage(this, "Invalid Input Entered\n" + rte.getMessage());
                return;
            }
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

        // invoked with update button action
        private void update() {
            try {
                Marine m = upInfoPan.getMarine();
                m.setGrade(upInfoPan.getGrade());
                m.setRoomNumber(upInfoPan.getRoom());
                m.setTier(upInfoPan.getTier());
                if (!updated) {
                    updated = true;
                }
                LeisureLog.infoMessage(this, "Marine Successfully Updated\n" + m);
                upInfoPan.clear();
            } catch (NumberFormatException nfe) {
                LeisureLog.errMessage(this, "Invalid Input \n" + nfe.getMessage());
            }
        }

    }

    // Panel for Marine info collection, used in both add and update panels
    private class InfoPanel extends JPanel {

        private JTextField dodTxt = new JTextField(10),
                firstTxt = new JTextField(10),
                midTxt = new JTextField(1),
                lastTxt = new JTextField(10),
                roomTxt = new JTextField(3);
        private final String[] tierStr = {"T1", "T2", "T3"};
        private final String[] rankStr = {"Pvt", "PFC", "LCpl", "Cpl", "Sgt"};
        private JComboBox tierCmb = new JComboBox(tierStr),
                rankCmb = new JComboBox(rankStr);
        private JButton popBtn = new JButton("Populate");
        private JLabel grLbl = new JLabel("E1");
        private Marine marine;

        // boolean determines if populate button displayed, for update panel
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
            // when populate boolean true add populate button
            if (pop) {
                c.anchor = GridBagConstraints.CENTER;
                c.gridx = 3;
                c.gridy = 0;
                c.gridwidth = 1;
                c.gridheight = 2;
                this.add(popBtn, c);
                popBtn.addActionListener(e -> populate());
                // name not valid to update
                firstTxt.setEditable(false);
                midTxt.setEditable(false);
                lastTxt.setEditable(false);
            }
        }

        // clears text fields
        private void clear() {
            dodTxt.setText("");
            firstTxt.setText("");
            midTxt.setText("");
            lastTxt.setText("");
            roomTxt.setText("");
            marine = null;
        }

        // getters for entered info
        private long getDODID() {
            return Long.parseLong(dodTxt.getText());
        }

        private String getFirst() {
            String str = firstTxt.getText();
            str = str.trim();
            if (str.isEmpty()) {
                throw new RuntimeException("First Name Field Empty");
            }
            return str;
        }

        private String getMid() {
            return midTxt.getText();
        }

        private String getLast() {
            String str = lastTxt.getText();
            str = str.trim();
            if (str.isEmpty()) {
                throw new RuntimeException("Last Name Field Empty");
            }
            return str;
        }

        private Marine.Grade getGrade() {
            switch (rankCmb.getSelectedIndex()) {
                case 0:
                    return Marine.Grade.E1;
                case 1:
                    return Marine.Grade.E2;
                case 2:
                    return Marine.Grade.E3;
                case 3:
                    return Marine.Grade.E4;
                case 4:
                    return Marine.Grade.E5;
                default:
                    return null;
            }
        }

        private int getRoom() {
            return Integer.parseInt(roomTxt.getText());
        }

        private Marine.Tier getTier() {
            switch (tierCmb.getSelectedIndex()) {
                case 0:
                    return Marine.Tier.T1;
                case 1:
                    return Marine.Tier.T2;
                case 2:
                    return Marine.Tier.T3;
                default:
                    return null;
            }
        }

        //getter for marine from populate action
        private Marine getMarine() {
            return marine;
        }

        // Update grade for rank selection
        private void grUpdate() {
            grLbl.setText("E" + (rankCmb.getSelectedIndex() + 1));
        }

        // invoked with populate button action, populates fields with existing Marine info
        private void populate() {
            try {
                marine = ms.lookup(getDODID());
                if (marine == null) {
                    LeisureLog.errMessage(this, "Marine Not Found For DODID " + getDODID());
                    return;
                }
                firstTxt.setText(marine.getFirstName());
                midTxt.setText(marine.getMid());
                lastTxt.setText(marine.getLastName());
                roomTxt.setText(String.valueOf(marine.getRoomNumber()));
                int grade = Integer.parseInt(marine.getGrade().toString().substring(1));
                rankCmb.setSelectedIndex(grade - 1);
                int tier = Integer.parseInt(marine.getTier().toString().substring(1));
                tierCmb.setSelectedIndex(tier - 1);
            } catch (NumberFormatException nfe) {
                LeisureLog.errMessage(this, "Entered DODID Not Valid\n" + nfe.getMessage());
            }
        }
    }
}
