import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Kayuk on 1/10/16.
 */
public class MultiThreadDownloader implements Runnable {
    private String path;
    private String dir;
    private InputStream input;
    private byte[] tempb;
    private int count_N;
    private PropertyChangeSupport pcs = null;
    private int progress = 0;
    private String tid;

    public MultiThreadDownloader(String path, String dir) {
        this.dir = dir;
        this.path = path;
    }

    public MultiThreadDownloader(String path, String dir, PropertyChangeSupport pcs, String id) {
        this.dir = dir;
        this.path = path;
        this.pcs = pcs;
        this.tid = id;
    }

    private void download() throws IOException {
        Thread.currentThread().setName(tid);

        Logger.getGlobal().info("Thread id is: " + Thread.currentThread().getName());
        long threadId = Thread.currentThread().getId();
        System.out.printf("Thread# %d starts.\n", threadId);
        // open connection
        URL link = new URL(this.path);
        HttpURLConnection conn = (HttpURLConnection) link.openConnection();

        // Open output file
        String filename = link.getFile().substring(link.getFile().lastIndexOf('/') + 1);
        String absAddress = dir + File.separator + filename;
        Logger.getGlobal().info(absAddress);
        RandomAccessFile raf = new RandomAccessFile(absAddress, "rw");
        long length = 0;

        // Check record-stop-point file, update the position
        String bakFileName = absAddress + ".bak";
        File backFile = new File(bakFileName);

        long pos = 0;
        if (backFile.exists()) {
            Scanner freader = new Scanner(backFile);
            try {
                pos = freader.nextLong();
            } catch (Exception ex) {
                System.out.print("Empty bak file.");
                ex.printStackTrace();
            }
            // Set HTTP REQUEST RANGE
            conn.setRequestProperty("Range", "bytes=" + pos + "-");
            length = conn.getContentLength() + pos;
            System.out.printf("We are now start downloading from the No.%d byte.\n", pos);
            freader.close();
        } else {
            length = conn.getContentLength();
            raf.setLength(length);
        }

        // Open input stream
        input = new BufferedInputStream(conn.getInputStream());
        // Open record-stop-point file to write
        PrintWriter writer = new PrintWriter(bakFileName);

        // Put contents of the input stream into the file by blocks of 1024 bytes
        raf.seek(pos);
        tempb = new byte[1024];
        count_N = 0;
        int flag = 0;
        // Use ExecutorService to handle connection loss;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    count_N = input.read(tempb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Logger.getGlobal().info("Here:" + Thread.currentThread().getName());

        try {
            Future future = executor.submit(task);
            future.get(15, TimeUnit.SECONDS);
            while (-1 != count_N) {
                raf.write(tempb, 0, count_N);
                pos += count_N;
                flag++;
                if (flag % 100 == 0) {
                    int percentage = (int)(pos*1.0 / length * 100);
                    //System.out.printf("Thread %d Downloading percentage %d %%\n", threadId, percentage);
                    if (percentage > this.progress){
                        if (pcs != null) {
                            Logger.getGlobal().info(Thread.currentThread().getName());
                            pcs.firePropertyChange(Thread.currentThread().getName(), this.progress ,percentage);
                        }
                        this.progress = percentage;
                    }
                }
                future = executor.submit(task);
                future.get(15, TimeUnit.SECONDS);
            }
        } catch (TimeoutException ex) {
            System.out.println("Lost Internet Connection...");
            ex.printStackTrace();
            writer.print(pos);
            return;
        } catch (InterruptedException ex) {
            // handle the interrupts
            System.out.println("Stop downloading...");
            ex.printStackTrace();
            writer.print(pos);
            return;
        } catch (ExecutionException e) {
            // handle other exceptions
            e.printStackTrace();
        } finally {
            raf.close();
            writer.close();
            conn.disconnect();
        }
        pcs.firePropertyChange(Thread.currentThread().getName(), 0 , 100);
        backFile.delete();
        System.out.printf("Thread# %d ends.\n", threadId);
    }

    @Override
    public void run() {
        try {
            download();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        File defaultFilePath = new File(".");
        String filePath = defaultFilePath.getAbsolutePath();
        String path1 = "https://download.jetbrains.com/webide/PhpStorm-10.0.3-custom-jdk-bundled.dmg";
        String path2 = "https://download.jetbrains.com/idea/ideaIC-15.0.2-custom-jdk-bundled.dmg";
//        String path2 = "http://bankinside.ru/sites/default/files/world-economy/tom-cruise.jpg";
//        String path1 = "http://cdn.playbuzz.com/cdn/1652d10b-884c-49c5-8450-59af40ca8832/9c36c2b7-cdd3-4baf-8c4a-af35f9371383_560_420.jpg";
        Runnable r1 = new MultiThreadDownloader(path1, filePath);
        Runnable r2 = new MultiThreadDownloader(path2, filePath);
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
    }
}
