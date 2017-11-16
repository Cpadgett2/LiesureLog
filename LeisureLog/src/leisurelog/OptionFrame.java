
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

        OptionFrame() {
            super("Marine Options");
            this.setSize(325, 270);
            this.setLocationRelativeTo(null);
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
                System.out.println("no save");
            }
            this.dispose();
        }

        // Remove Marine panel 
        private class RemovePanel extends JPanel {

            private JButton remBtn = new JButton("Delete Marine");
            private LeisureLog.MarinePanel mpOp = new LeisureLog.MarinePanel();

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

