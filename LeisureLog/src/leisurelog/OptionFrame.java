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
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Frame for marine management user interface for add, remove and delete
 * functions
 *
 * @author TeamLeisure
 */
class OptionFrame extends JFrame {

    private static final long serialVersionUID = 7308024742648619776L;
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
            if (i == 0) { // yes save changes
                File f = LeisureLog.chooseFile(this, "Save To File");
                if (f == null) {
                    return;
                }
                try {
                    ms.toFile(f);
                    LeisureLog.setMarineFile(f.toPath());
                    LeisureLog.writeConfig();
                } catch (IOException ioe) {
                    LeisureLog.errMessage(this,
                            "Unable To Export Marine Structure\n"
                            + ioe.getMessage());
                }
            }
        }
        this.dispose();
    }

    // Remove Marine panel 
    private class RemovePanel extends JPanel {

        private static final long serialVersionUID = -8506621070511999012L;
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
                LeisureLog.errMessage(this, "No Marine Selected\n"
                        + "Verify DODID Lookup");
                return;
            }
            if (!ms.remove(m)) {
                LeisureLog.errMessage(this, "Marine Record Not Found, "
                        + "Unable to Delete");
                return;
            }
            if (!updated) {
                updated = true;
            }
            LeisureLog.infoMessage(this, "Successfully Removed Marine \n" + m);
            lkPan.clear();
        }

    }

    // Add new Marine panel
    private class AddPanel extends JPanel {

        private static final long serialVersionUID = -4710847913264633731L;
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
                    LeisureLog.errMessage(this, "Duplicate DODID: "
                            + "Record With This DODID Already Exists");
                    return;
                } else {
                    LeisureLog.infoMessage(this, "Successfully Added Marine\n " + m);
                    if (!updated) {
                        updated = true;
                    }
                    infoPan.clear();
                }
            } catch (IllegalArgumentException iae) {
                LeisureLog.errMessage(this, "Invalid Input:\n"
                        + iae.getMessage());
                return;
            }
        }
    }

    // Panel for updating existing Marine
    private class UpdatePanel extends JPanel {

        private static final long serialVersionUID = -1935787367895541868L;
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
                if (m == null){
                    LeisureLog.errMessage(this, "Update Panel Has "
                            + "Not Been Populated");
                    return;
                }
                if (m.getDODID() != upInfoPan.getDODID()) {
                    LeisureLog.errMessage(this, "DODID Field Has Been Modified\n"
                            + "Updates Can Not Be Made To This Field");
                    return;
                }
                if (m.setGrade(upInfoPan.getGrade())
                        || m.setRoomNumber(upInfoPan.getRoom())
                        || m.setTier(upInfoPan.getTier())) {
                    if (!updated) {
                        updated = true;
                    }
                    LeisureLog.infoMessage(this, "Marine Successfully Updated\n" + m);
                    upInfoPan.clear();
                } else {
                    LeisureLog.errMessage(this, "No Modifications Made "
                            + "To Updatable Fields\n"
                            + "Updatable Fields: Grade, Rank, Tier");
                }
            } catch (IllegalArgumentException  iae) {
                LeisureLog.errMessage(this, "Invalid Input:\n" + iae.getMessage());
            }
        }

    }

    // Panel for Marine info collection, used in both add and update panels
    private class InfoPanel extends JPanel {

        private static final long serialVersionUID = 6517781526856784060L;
        private JTextField dodTxt = new JTextField(10),
                firstTxt = new JTextField(10),
                midTxt = new JTextField(1),
                lastTxt = new JTextField(10),
                roomTxt = new JTextField(3);
        private final String[] tierStr = {"T1", "T2", "T3"};
        private final String[] rankStr = {"Pvt", "PFC", "LCpl", "Cpl", "Sgt"};
        private JComboBox<String> tierCmb = new JComboBox<>(tierStr),
                rankCmb = new JComboBox<>(rankStr);
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
            tierCmb.setSelectedIndex(0);
            rankCmb.setSelectedIndex(0);
            marine = null;
        }

        // getters for info from components
        private long getDODID() {
            String str = dodTxt.getText().trim();
            if (str.isEmpty()) {
                throw new IllegalArgumentException("DODID Field Empty");
            }
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException("DODID Must Be Numeric Value\n"
                        + nfe.getMessage());
            }
        }

        private String getFirst() {
            String str = firstTxt.getText().trim();
            if (str.isEmpty()) {
                throw new IllegalArgumentException("First Name Field Empty");
            }
            return str;
        }

        private String getMid() {
            return midTxt.getText();
        }

        private String getLast() {
            String str = lastTxt.getText().trim();
            if (str.isEmpty()) {
                throw new IllegalArgumentException("Last Name Field Empty");
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
            String str = roomTxt.getText().trim();
            if (str.isEmpty()) {
                throw new IllegalArgumentException("Room Field Empty");
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException("Room Must Be Numeric Value\n"
                        + nfe.getMessage());
            }
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
                    LeisureLog.errMessage(this, "Marine Record Not Found For DODID: "
                            + getDODID());
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
            } catch (IllegalArgumentException iae) {
                LeisureLog.errMessage(this, "Invalid Input:\n"
                        + iae.getMessage());
            }
        }
    }
}
