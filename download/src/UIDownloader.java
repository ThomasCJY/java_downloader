
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kayuk on 1/10/16.
 */
public class UIDownloader extends JPanel
        implements ActionListener, PropertyChangeListener{
    ExecutorService pool;
    JButton dirButton, taskButton, startButton, stopButton;
    JPanel panel, prgPanel;
    JFileChooser fc;
    JTextField jfield;
    HashMap<String, JProgressBar> jbarList;
    File savePath;
    AtomicInteger counter;
    HashMap<String, String[]> mapList;

    public UIDownloader(){
        jbarList = new HashMap<String, JProgressBar>();
        mapList= new HashMap<String, String[]>();
        pool = Executors.newFixedThreadPool(5);
        counter = new AtomicInteger(1);
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

        panel = new JPanel(new GridBagLayout());
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


        prgPanel = new JPanel();
        prgPanel.setLayout(new BoxLayout(prgPanel, BoxLayout.Y_AXIS));

        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(prgPanel,c);

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
        }else if (e.getSource() == taskButton){
            String address = JOptionPane.showInputDialog(
                    "Please input the url of the resource",
                    "http://cdn.playbuzz.com/cdn/1652d10b-884c-49c5-8450-59af40ca8832/9c36c2b7-cdd3-4baf-8c4a-af35f9371383_560_420.jpg");
            if (address == null) return;
            Logger.getGlobal().info(address);

            JProgressBar jbar= new JProgressBar(0, 100);
            jbar.setValue(0);
            jbar.setStringPainted(true);

            PropertyChangeSupport pcs = new PropertyChangeSupport(this);
            String threadName = String.valueOf(counter.getAndIncrement());

            JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(threadName);
            JLabel pathLabel = new JLabel(savePath.getAbsolutePath());
            nameLabel.setSize(50, 50);
            pathLabel.setSize(400, 50);
            tempPanel.add(nameLabel);
            tempPanel.add(pathLabel);
            tempPanel.add(jbar);
            prgPanel.add(tempPanel);
            prgPanel.revalidate();
            validate();

            Runnable task = new MultiThreadDownloader(address, savePath.getAbsolutePath(), pcs, threadName);
            pcs.addPropertyChangeListener(this);
            jbarList.put(threadName, jbar);

            String[] stg = {address, savePath.getAbsolutePath()};
            mapList.put(threadName, stg);

            pool.submit(task);
        }else if(e.getSource() == stopButton){
            if (pool.isShutdown()) return;
            else pool.shutdownNow();
        }else if(e.getSource() == startButton){
            // restart
            // address, savePath, threadName needed
            pool = Executors.newFixedThreadPool(5);
            for(String tName : mapList.keySet()){
                String[] slist = mapList.get(tName);
                PropertyChangeSupport pcs = new PropertyChangeSupport(this);
                Runnable task = new MultiThreadDownloader(slist[0], slist[1], pcs, tName);
                pcs.addPropertyChangeListener(this);
                JProgressBar jProgressBar= jbarList.get(tName);
                pool.submit(task);
            }

        }

    }

    private static void createAndShowGUI(){
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new UIDownloader());
        jFrame.setSize(650,300);
        jFrame.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String tname = evt.getPropertyName();
        int progress = (Integer)evt.getNewValue();
        JProgressBar jbar = jbarList.get(tname);
        jbar.setValue(progress);
        Logger.getGlobal().info("New Value = " + evt.getNewValue());

        if (progress == 100){
            mapList.remove(tname);
        }
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
