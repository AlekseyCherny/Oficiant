package ua.yalta.oficiant.refs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 27.10.12
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class Zals extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

    }


    //
    private void saveZalsTablesToFile(String zal_code) {
        String filename = "zal" + zal_code;
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readZalsTableFromFile(String zal_code) {
        String fileName = "zal" + zal_code;
        String outputLine;
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((outputLine = reader.readLine()) != null) {
                outputLine.split(";");
            }

            reader.close();
//            InputStreamReader isr = new InputStreamReader(fis);
//            StringBuilder sb = new StringBuilder();
//            char[] inputBuffer = new char[2048];
//            int l;
//            while ((l = isr.read(inputBuffer)) != -1) {
//                sb.append(inputBuffer, 0, l);
//            }
//            String readString = sb.toString();


            ///Log.i("LOG_TAG", "Read string: " + readString);
            // deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }

}
