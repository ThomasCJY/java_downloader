import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Kayuk on 1/9/16.
 */
public class InterruptedDownloader {
    public static String path_bigfile = "https://download.jetbrains.com/webide/PhpStorm-10.0.3-custom-jdk-bundled.dmg";
    public static String path = "http://cdn.playbuzz.com/cdn/1652d10b-884c-49c5-8450-59af40ca8832/9c36c2b7-cdd3-4baf-8c4a-af35f9371383_560_420.jpg";

    public void download(String surl, int stopPoint) throws IOException{
        // Build a new URL, open connection
        URL link = new URL(surl);
        HttpURLConnection conn = (HttpURLConnection) link.openConnection();

        // Open output file
        String filename = link.getFile().substring(link.getFile().lastIndexOf('/')+1);
        RandomAccessFile raf = new RandomAccessFile(filename, "rw");
        long length = 0;

        // Check record-stop-point file, update the position
        String bakFileName = filename + ".bak";
        File backFile = new File(bakFileName);
        long pos = 0;
        if (backFile.exists()){
            Scanner freader = new Scanner(backFile);
            pos = freader.nextLong();
            // Set HTTP REQUEST RANGE
            conn.setRequestProperty("Range", "bytes="+pos+"-");
            length = conn.getContentLength() + pos;
            System.out.printf("We are now start downloading from the No.%d byte.\n", pos);
            freader.close();
        }else{
            length = conn.getContentLength();
            raf.setLength(length);
        }

        // Open input stream
        InputStream input = new BufferedInputStream(conn.getInputStream());
        // Open record-stop-point file to write
        PrintWriter writer = new PrintWriter(bakFileName);


        // Put contents of the input stream into the file by blocks of 1024 bytes
        raf.seek(pos);
        byte[] tempb = new byte[1024];
        int n = 0;
        while(-1!=(n=input.read(tempb))){
            if(stopPoint > 1024 && pos > stopPoint) break;
            raf.write(tempb,0,n);
            pos += n;
            if (pos % 30 == 2) System.out.printf("Downloading percentage %f %%\n", pos*1.0/length * 100);
        }
        raf.close();
        writer.print(pos);
        writer.close();
        conn.disconnect();
    }

    public static void main(String[] args) throws IOException{
        String path = InterruptedDownloader.path_bigfile;

        InterruptedDownloader idemo = new InterruptedDownloader();
        // Use console to control the interrupted point
        int spoint = 0;
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("Please enter the byte you want the download to stop at. Enter -1 to skip. (>1024)");
            spoint = in.nextInt();
            if (spoint >= 0 && spoint < 1024){
                System.out.println("Please enter a valid stop point. (>1024)");
            }else{
                break;
            }
        }

        idemo.download(path, spoint);
        if (spoint != -1){
            System.out.println("Now we stop the download, please check the document. Enter C to continue.");
            while (true){
                String c = in.nextLine();
                if (c.equals("C")) break;
            }
            in.close();
            idemo.download(path, -1);
        }
        System.out.println("Finished");
    }
}
