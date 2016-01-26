
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Kayuk on 1/10/16.
 */
public class UIDownloader extends JPanel
        implements ActionListener{
    private JButton dirButton;

    public UIDownloader(){
        dirButton = new JButton("Location");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dirButton);
        add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == dirButton){
//
//        }
    }

    private static void createAndShowGUI(){
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new UIDownloader());
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

    }

}
