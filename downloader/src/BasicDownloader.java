import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by Kayuk on 10/4/15.
 */
public class BasicDownloader {

    public static void main(String[] args) throws IOException{
        String fileName = "file.txt"; //The file that will be saved on your computer
        URL link = new URL("http://shayconcepts.com"); //The file that you want to download

        //Code to download
        InputStream in = new BufferedInputStream(link.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();
        //End download code

        System.out.println("Finished");
    }


}
