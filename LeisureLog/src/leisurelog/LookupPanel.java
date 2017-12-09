package leisurelog;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel for lookup and display of Marine info based on DODID
 * @author TeamLeisure
 */
public class LookupPanel extends JPanel {
    private static final long serialVersionUID = 5471409722811675464L;
    // panel components
    private JTextField jtfID = new JTextField(10);
    private JButton lkBtn = new JButton("Lookup"),
            clrBtn = new JButton("Clear");
    private JLabel nameLbl = new JLabel(""),
            rankLbl = new JLabel(""),
            rmLbl = new JLabel(""),
            tierLbl = new JLabel(""),
            idLbl = new JLabel(""),
            grLbl = new JLabel("");
    // marine lookup
    private final MarineStructure ms;
    private Marine marine;

    // constructor builds panel
    LookupPanel(MarineStructure ms) {
        this.ms = ms;
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
        jtfID.addActionListener(l -> lookup());
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
    

    // invoked with lookup button action, gets Marine from Marine Structure
    private void lookup() {
        try {
            String idStr = jtfID.getText().trim();
            if (idStr.isEmpty()){
                throw new IllegalArgumentException("DODID Field Empty");
            }
            long id = Long.parseLong(idStr);
            marine = ms.lookup(id);
            if (marine == null){
                int i = JOptionPane.showConfirmDialog(this, "Marine Record Not Found For DODID: " 
                        + id + "\nGo To Add Marine Option?", "Marine Not Found",
                        JOptionPane.YES_NO_OPTION);
                if (i == 0) new OptionFrame(ms);
                return;
            }
            nameLbl.setText(marine.getName());
            rankLbl.setText(marine.getRank());
            rmLbl.setText(String.valueOf(marine.getRoomNumber()));
            tierLbl.setText(marine.getTier().toString());
            idLbl.setText(String.valueOf(marine.getDODID()));
            grLbl.setText(marine.getGrade().toString());
            jtfID.setText("");
        } catch (NumberFormatException nfe) {
            LeisureLog.errMessage(this, "DODID Must Be Numeric Value\n" 
                    + nfe.getMessage());
        } catch (IllegalArgumentException iae){
            LeisureLog.errMessage(this, "Invalid Input: \n" + iae.getMessage());
        }

    }

    // invoked with clear button action, clears panel labels, resets marine
    public void clear() {
        nameLbl.setText(" ");
        rankLbl.setText(" ");
        rmLbl.setText(" ");
        tierLbl.setText(" ");
        idLbl.setText(" ");
        grLbl.setText(" ");
        marine = null;
    }

    // returns marine currently on display in panel
    public Marine getMarine() {
        return marine;
    }
}
