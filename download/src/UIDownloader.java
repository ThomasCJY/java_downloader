
import javax.swing.*;
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
    JButton dirButton;
    JFileChooser fc;
    JTextField jfield;
    File savePath;

    public UIDownloader(){
        savePath = new File(".");

        jfield = new JTextField(20);
        jfield.setEditable(false);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        dirButton = new JButton("Choose Path");
        dirButton.addActionListener(this);



        JPanel buttonPanel = new JPanel();

        buttonPanel.add(dirButton);
        buttonPanel.add(jfield);
        add(buttonPanel);
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
