package ua.yalta.oficiant.net;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Socket1c {
    public Socket1c() {
        super();
    }

    public static List<String> writeToSocketAndGet(String ip, String port, String input) {
        List<String> dataList = new ArrayList<String>();
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        String outputLine;
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "cp1251"));
            writer.write(input + "\n", 0, input.length() + 1);
            writer.flush();
            while ((outputLine = reader.readLine()) != null) {
                //Log.d("LINE",outputLine);
                dataList.add(outputLine);
            }

        } catch (IOException e) {
            dataList.add("ERROR;SOCKET ERROR");
            return dataList;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                // Log.e("Baya", " " + "WRITER" + " WRITER calling socket", e);
            }
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                //Log.e("Baya", " " + "READER" + " READER calling socket", e);
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                // Log.e("Baya", " " + "SOCKET" + " SOCKET calling socket", e);

            }
        }

        return dataList;
    }
}
