
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kayuk on 1/10/16.
 */
public class UIDownloader extends JPanel
        implements ActionListener{
    JButton dirButton, taskButton, startButton, stopButton;
    JFileChooser fc;
    JTextField jfield;
    File savePath;

    public UIDownloader(){
        savePath = new File(".");

        jfield = new JTextField(40);
        jfield.setEditable(false);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        dirButton = new JButton("Choose Path");
        dirButton.addActionListener(this);

        taskButton = new JButton("New Task");
        taskButton.addActionListener(this);

        stopButton = new JButton("Stop All");
        stopButton.addActionListener(this);

        startButton = new JButton("Restart All");
        startButton.addActionListener(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(dirButton,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(jfield,c);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(taskButton,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        panel.add(stopButton,c);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        panel.add(startButton,c);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dirButton){
            int returnVal = fc.showOpenDialog(UIDownloader.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                savePath = fc.getSelectedFile();
                //This is where a real application would open the file.
                Logger.getGlobal().info(savePath.getAbsolutePath());
                jfield.setText(savePath.getAbsolutePath());
            } else {
                Logger.getGlobal().info("Cancel Path Choosing");
            }
        }if (e.getSource() == taskButton){
            String address = JOptionPane.showInputDialog(
                    "Please input the url of the resource",
                    "http://cdn.playbuzz.com/cdn/1652d10b-884c-49c5-8450-59af40ca8832/9c36c2b7-cdd3-4baf-8c4a-af35f9371383_560_420.jpg");
            Logger.getGlobal().info(address);

            Runnable task = new MultiThreadDownloader(address, savePath.getAbsolutePath());
            Thread thread = new Thread(task);

            thread.start();
        }

    }

    private static void createAndShowGUI(){
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new UIDownloader());
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void main(String[] args){
//        Logger.getGlobal().setLevel(Level.OFF);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

    }

}
