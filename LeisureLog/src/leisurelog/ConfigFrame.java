package leisurelog;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Configuration frame options for user selection of Marine data file and 
 * log export directory
 *
 * @author TeamLeisure
 */
public class ConfigFrame extends JFrame {
    private static final long serialVersionUID = -7246437631522970335L;
    private JButton okBtn = new JButton("OK"), cancelBtn = new JButton("Cancel");
    private MarineStructure ms;
    // nested class panels
    private FilePanel fp;
    private DirPanel dp;
    // has been updated flag
    private boolean fileUpdate = false;

    ConfigFrame(MarineStructure ms) {
        super("Configuration Settings");
        this.ms = ms;
        fp = new FilePanel();
        dp = new DirPanel();
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.add(fp);
        this.add(dp);
        this.add(Box.createHorizontalStrut(200));
        okBtn.setPreferredSize(cancelBtn.getPreferredSize());
        this.add(okBtn);
        okBtn.addActionListener(l -> ok());
        this.add(cancelBtn);
        cancelBtn.addActionListener(l -> cancel());
        this.setResizable(false);
        this.setVisible(true);
    }

    // invoked with ok button action, updates paths, config, exits
    private void ok() {
        apply();
        if (fileUpdate) {
            int i = LeisureLog.conMessage(this, "Rebuild Marine Structure From File?");
            if (i == 0) {
                try {
                    ms.reBuild(fp.file.toFile());
                } catch (IOException ioe) {
                    LeisureLog.errMessage(this, "Unable To Rebuild Structure\n" + ioe.getMessage());
                }
            }
        }
        this.dispose();
    }

    // send changes to leisurelog
    private void apply() {
        LeisureLog.setMarineFile(fp.file);
        LeisureLog.setLogDirectory(dp.dir);
        LeisureLog.writeConfig();
    }

    // cancel button action, exits without save
    private void cancel() {
        this.dispose();
    }

    // inner panel for selection of marine data file path
    private class FilePanel extends JPanel {
        private static final long serialVersionUID = 5672019349616902840L;
        private JTextField jtfFile = new JTextField(25);
        private JButton browseFile = new JButton("Browse");
        private Path file;

        FilePanel() {
            this.file = LeisureLog.getMarineFile();
            this.setBorder(BorderFactory.createTitledBorder("Marine Data File"));
            this.setLayout(new FlowLayout());
            this.add(jtfFile);
            jtfFile.setEditable(false);
            if (file != null) {
                jtfFile.setText(file.toString());
            }
            browseFile.addActionListener(l -> browseFile());
            this.add(browseFile);
        }

        // invoked with browse button action, prompts user to select file
        private void browseFile() {
            File f;
            if (file == null) {
                f = LeisureLog.chooseFile(this, "Select Marine Data File");
            } else {
                f = LeisureLog.chooseFile(this, "Select Marine Data File",
                        file.toString());
            }
            if (f != null && f.canRead()) {
                jtfFile.setText(f.getAbsolutePath());
                file = f.toPath();
                fileUpdate = true;
            } else if (f != null){
                LeisureLog.errMessage(this, "Selected File Not Readable");
            }
        }
    }

    // inner panel for log directory path
    private class DirPanel extends JPanel {
        private static final long serialVersionUID = -7928922845412221148L;
        private JTextField jtfDir = new JTextField(25);
        private JButton browseDir = new JButton("Browse");
        private Path dir;

        DirPanel() {
            this.dir = LeisureLog.getLogDirectory();
            this.setBorder(BorderFactory.createTitledBorder("Log Publish Directory"));
            this.setLayout(new FlowLayout());
            this.add(jtfDir);
            jtfDir.setEditable(false);
            if (dir != null) {
                jtfDir.setText(dir.toString());
            }
            browseDir.addActionListener(l -> browseDirectory());
            this.add(browseDir);
        }

        // invoked with browse button action, prompts user for directory select
        private void browseDirectory() {
            File f;
            if (dir == null) {
                f = LeisureLog.chooseFile(this,
                        "Select Directory For Log Publications",
                        JFileChooser.DIRECTORIES_ONLY, ".");
            } else {
                f = LeisureLog.chooseFile(this,
                        "Select Directory For Log Publications",
                        JFileChooser.DIRECTORIES_ONLY, dir.toString());
            }
            if (f != null && f.isDirectory()) {
                jtfDir.setText(f.getAbsolutePath());
                dir = f.toPath();
            } else if (f != null){
                LeisureLog.errMessage(this, "Invalid Directory Selected");
            }
        }

    }
}
