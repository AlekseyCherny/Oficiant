package ua.yalta.oficiant;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.exchange.Downloader;
import ua.yalta.oficiant.exchange.Exporter;
import ua.yalta.oficiant.exchange.Importer;
import ua.yalta.oficiant.net.Socket1c;
import ua.yalta.oficiant.utils.PosPrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 04.11.12
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */





public class TablesActivity extends Activity implements
       ActionBar.OnNavigationListener {



    private static final int[]	lookup =
            {
                    0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
                    0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x000E, 0x000F,
                    0x0010, 0x0011, 0x0012, 0x0013, 0x0014, 0x0015, 0x0016, 0x0017,
                    0x0018, 0x0019, 0x001A, 0x001B, 0x001C, 0x001D, 0x001E, 0x001F,
                    0x0020, 0x0021, 0x0022, 0x0023, 0x0024, 0x0025, 0x0026, 0x0027,
                    0x0028, 0x0029, 0x002A, 0x002B, 0x002C, 0x002D, 0x002E, 0x002F,
                    0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037,
                    0x0038, 0x0039, 0x003A, 0x003B, 0x003C, 0x003D, 0x003E, 0x003F,
                    0x0040, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047,
                    0x0048, 0x0049, 0x004A, 0x004B, 0x004C, 0x004D, 0x004E, 0x004F,
                    0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057,
                    0x0058, 0x0059, 0x005A, 0x005B, 0x005C, 0x005D, 0x005E, 0x005F,
                    0x0060, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067,
                    0x0068, 0x0069, 0x006A, 0x006B, 0x006C, 0x006D, 0x006E, 0x006F,
                    0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077,
                    0x0078, 0x0079, 0x007A, 0x007B, 0x007C, 0x007D, 0x007E, 0x007F,
                    0x0410, 0x0411, 0x0412, 0x0413, 0x0414, 0x0415, 0x0416, 0x0417,
                    0x0418, 0x0419, 0x041A, 0x041B, 0x041C, 0x041D, 0x041E, 0x041F,
                    0x0420, 0x0421, 0x0422, 0x0423, 0x0424, 0x0425, 0x0426, 0x0427,
                    0x0428, 0x0429, 0x042A, 0x042B, 0x042C, 0x042D, 0x042E, 0x042F,
                    0x0430, 0x0431, 0x0432, 0x0433, 0x0434, 0x0435, 0x0436, 0x0437,
                    0x0438, 0x0439, 0x043A, 0x043B, 0x043C, 0x043D, 0x043E, 0x043F,
                    0x2591, 0x2592, 0x2593, 0x2502, 0x2524, 0x2561, 0x2562, 0x2556,
                    0x2555, 0x2563, 0x2551, 0x2557, 0x255D, 0x255C, 0x255B, 0x2510,
                    0x2514, 0x2534, 0x252C, 0x251C, 0x2500, 0x253C, 0x255E, 0x255F,
                    0x255A, 0x2554, 0x2569, 0x2566, 0x2560, 0x2550, 0x256C, 0x2567,
                    0x2568, 0x2564, 0x2565, 0x2559, 0x2558, 0x2552, 0x2553, 0x256B,
                    0x256A, 0x2518, 0x250C, 0x2588, 0x2584, 0x258C, 0x2590, 0x2580,
                    0x0440, 0x0441, 0x0442, 0x0443, 0x0444, 0x0445, 0x0446, 0x0447,
                    0x0448, 0x0449, 0x044A, 0x044B, 0x044C, 0x044D, 0x044E, 0x044F,
                    0x0401, 0x0451, 0x0404, 0x0454, 0x0407, 0x0457, 0x040E, 0x045E,
                    0x00B0, 0x2219, 0x00B7, 0x221A, 0x2116, 0x00A4, 0x25A0, 0x00A0
            };




    public static String CODEPAGE="cp1251";//US-ASCII

    public static String ASCI="US-ASCII";//US-ASCII

    public static String INITPRINTER;//ESC @

    public static String PAGE_MODE;//ESC L
    public static String PRINT_IN_PAGEMODE;//FF or ESC FF
    public static String PRINT_PAGEMODE;// ESC FF
    public static String CAMCEL_BUF;// ESC FF
    public static String NORMAL_MODE;//ESC S

    public static String FONTBIG;
    public static String FONTSMAL;

    public static String FONT_DOUBLE;
    public static String FONT_DOUBLE_CANCEL;

    public static String LF;
    public static String FONT_GIRNO_CANCEL;

    public static String CUT;
    public static String FONT_GIRNO;

    public static String DOUBLE_SIZE;
    public static String DEF_SIZE;

    public static String CENTER_POS;
    public static String LEFT_POS;
    public static String RIGHT_POS;



    final String LOG_TAG = "myLogs";
    String currentZal;
    List<String> data;
  //  int dpWidth;
  //  int dpHeight;
  //  int pixelW;
  //  int pixelH;
  //  float Dens;
    List<String> MenuZals;
    Map<String, String> mZals;
    String ServerIP;
    String ServerPort;

 TextView txtStatus;


    private static int findIndex(int value) {
        for(int i=0; i<lookup.length; i++)
            if(lookup[i] == value)
                return i;
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        txtStatus=(TextView)findViewById(R.id.textViewStatus);


        MenuZals = new ArrayList<String>();
        mZals = new HashMap<String, String>();
        Cursor mCursor = DBConnector.instance(Config.context).getZals();
        while (mCursor.moveToNext()) {
            mZals.put(mCursor.getString(2), mCursor.getString(1));//key-имя зала значение-код зала
            MenuZals.add(mCursor.getString(2));
            mCursor.moveToNext();
        }
        mCursor.close();
        readPrefs();

        //TODO код зала
        currentZal = "2";

        //TODO столы зала
      //  Cursor tablesCursor = DBConnector.instance(Config.context).getTablesByZalCode(currentZal);

      //  tablesCursor.close();

      //  pixelW = this.getWindowManager().getDefaultDisplay().getWidth();
      //  pixelH = this.getWindowManager().getDefaultDisplay().getHeight();
     //   dpWidth = (int) (pixelW * getResources().getDisplayMetrics().density);
      //  dpHeight = (int) (pixelH * getResources().getDisplayMetrics().density);
     //   Dens = getResources().getDisplayMetrics().density;


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, MenuZals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setListNavigationCallbacks(adapter, this);

        //!!!!!! bar.setSelectedNavigationItem(1);
    }


    public static void initConstants()throws UnsupportedEncodingException {
        INITPRINTER =new String(new byte[] {(byte)0x1b,(byte)0x40}, ASCI); //ESC @

        LF =new String(new byte[] {(byte)0x1a}, ASCI);

        PAGE_MODE =new String(new byte[] {(byte)0x1b,(byte)0x4c}, ASCI);//ESC L
        PRINT_IN_PAGEMODE =new String(new byte[] {(byte)0x0c}, ASCI);//FF

        PRINT_PAGEMODE =new String(new byte[] {(byte)0x1b,(byte)0x0c}, ASCI);//ESC FF

        NORMAL_MODE =new String(new byte[] {(byte)0x1b,(byte)0x53}, ASCI);//ESC S

        FONTBIG =new String(new byte[] {(byte)0x1b,(byte)0x21,(byte)0}, ASCI);
        FONTSMAL=new String(new byte[] {(byte)0x1b,(byte)0x21,(byte)1}, ASCI);

        FONT_DOUBLE=new String(new byte[] {(byte)0x1b,(byte)0x47,(byte)1}, ASCI);
        FONT_DOUBLE_CANCEL=new String(new byte[] {(byte)0x1b,(byte)0x47,(byte)0}, ASCI);

        FONT_GIRNO=new String(new byte[] {(byte)0x1b,(byte)0x45,(byte)1}, ASCI);
        FONT_GIRNO_CANCEL=new String(new byte[] {(byte)0x1b,(byte)0x45,(byte)0}, ASCI);

        CUT=new String(new byte[] {(byte)0x1d,(byte)0x56,(byte)0x01}, ASCI); //GS V n

        DOUBLE_SIZE=new String(new byte[] {(byte)0x1d,(byte)0x21,(byte)17}, ASCI); //GS !
        DEF_SIZE=new String(new byte[] {(byte)0x1d,(byte)0x21,(byte)0}, ASCI); //GS !

        CENTER_POS=new String(new byte[] {(byte)0x1b,(byte)0x61,(byte)1}, ASCI); //ESC a
        LEFT_POS=new String(new byte[] {(byte)0x1b,(byte)0x61,(byte)0}, ASCI); //ESC a
        RIGHT_POS=new String(new byte[] {(byte)0x1b,(byte)0x61,(byte)2}, ASCI); //ESC a
    }



    public void startPrint(View v) throws UnsupportedEncodingException {
       initConstants();

        final Socket[] socket = {null};
        final DataOutputStream[] dataOutputStream = {null};
        final DataInputStream[] dataInputStream = {null};


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket[0] = new Socket("192.168.1.200", 9100);

                    dataOutputStream[0] = new DataOutputStream(socket[0].getOutputStream());
                    dataInputStream[0] = new DataInputStream(socket[0].getInputStream());
                    dataOutputStream[0].write(getDocumentToPrint(), 0, getDocumentToPrint().length);
                    Thread.sleep(100);
                    System.out.print("Starting");
                    //dataOutputStream.writeUTF(textOut.getText().toString());
                    //textIn.setText(dataInputStream.readUTF());
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (socket[0] != null) {
                        try {
                            socket[0].close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (dataOutputStream[0] != null) {
                        try {
                            dataOutputStream[0].close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (dataInputStream[0] != null) {
                        try {
                            dataInputStream[0].close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();


    }

    public static String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }


    public static byte[] getDocumentToPrint() throws UnsupportedEncodingException {

      //  byte[] RAMKAB={(byte)0xc9,(byte)0xcd,(byte)0xcd,(byte)0xcd,(byte)0xcd,(byte)0xcd,(byte)0xcd,(byte)0xbb,(byte)0x0a};
      //  String RAMKA=new String(RAMKAB, "cp866");


       // byte[] escByte={(byte)0x1b};
       // String escStr=new String(escByte, "cp866");
      //  String LINESPASE4=escStr+"J4";
      //  String FEED2=escStr+"d2";
        //   String LEFTPOS=escStr+"a0";

        //  byte[] DOUBLE_HWBYTE={(byte)0x1b,(byte)0x21,(byte)49};//48-49
        //  String DOUBLE_HW=new String(DOUBLE_HWBYTE, "cp866");

        //byte[] FONTSMALBYTE={(byte)0x1b,(byte)0x21,(byte)1};
        // byte[] FONTBIGBYTE={(byte)0x1b,(byte)0x21,(byte)0};

        // String FONTSMAL=new String(FONTSMALBYTE, "cp866");
        // String FONTBIG=new String(FONTBIGBYTE, "cp866");

        // byte[] escCenter = { (byte)0x1b,(byte)0x61,(byte)0x01};//ESC a 1 - senter position

        // String str = new String(escCenter, "cp866");

   //     String VERT =new String(new byte[] {(byte) 0xb3}, "cp866");

  //      String LEFTTOP=new String(new byte[]{(byte)0xda},"cp866");
   //     String RIGHTTOP=new String(new byte[]{(byte)0xbf},"cp866");
  //      String LINE=new String(new byte[]{(byte)0xc4},"cp866");

  //      String LEFTBOT=new String(new byte[]{(byte)0xc0},"cp866");
  //      String RIGHBOT=new String(new byte[]{(byte)0xd9},"cp866");


        //   String UNI =new String(new byte[] {(byte)0x1b,(byte) 0x55,(byte) 0x01}, "cp866");

//        String LINESPASE0 =new String(new byte[] {(byte)0x1b,(byte) 0x33,(byte) 0x00}, "cp866");


 //       String PRINTAREA1=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)0,(byte)0,(byte)0,(byte)0,(byte)44,(byte)1,(byte)192,(byte)0}, "cp866");
 //       String PRINTAREA2=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)54,(byte)1,(byte)0,(byte)0,(byte)96,(byte)0,(byte)192,(byte)0}, "cp866");

 //       String PRINTAREA3=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)0,(byte)0,(byte)192,(byte)0,(byte)44,(byte)1,(byte)192,(byte)0}, "cp866");
  //      String PRINTAREA4=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)54,(byte)1,(byte)192,(byte)0,(byte)96,(byte)0,(byte)192,(byte)0}, "cp866");

  //      String PRINTAREA5=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)0,(byte)0,(byte)128,(byte)1,(byte)44,(byte)1,(byte)192,(byte)0}, "cp866");
  //      String PRINTAREA6=new String(new byte[] {(byte)0x1b,(byte) 0x57,(byte)54,(byte)1,(byte)128,(byte)1,(byte)96,(byte)0,(byte)192,(byte)0}, "cp866");

   //     String MOTION=new String(new byte[] {(byte)0x1d,(byte) 0x50,(byte)200,(byte)180}, "cp866");


  //      String PRINTAREAW1=new String(new byte[] {(byte)0x1d,(byte) 0x57,(byte)180,(byte)0}, "cp866");
  //      String LEFTMARGIN=new String(new byte[] {(byte)0x1d,(byte) 0x4c,(byte)190,(byte)0}, "cp866");


   //     String SETBEGIN=new String(new byte[] {(byte)0x1d,(byte) 0x54,(byte)49}, "cp866");

        String hello="Водка NEMIROF платиновая Голд Super";
        String hello2="     1200,00";

        String hello22="Привет мир длинная строка пришла сюда и заняла все место";
        String hello23="      100,00";

        String hello32="Сок Сандора в ассортименте";
        String hello33="5 100,00";

        String strresini=INITPRINTER +FONTSMAL+DOUBLE_SIZE+CENTER_POS+"магазин Супермен"+"\n\n\n"+FONTBIG+DEF_SIZE+LEFT_POS;//+FONT_GIRNO;//LINESPASE0


        //String strresini=INITPRINTER +UNI+LINESPASE0+str+FONTBIG;
        // String s1=LEFTTOP+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+RIGHTTOP+"\n"+VERT+FONT_GIRNO+hello+VERT+"\n"+LEFTBOT+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+RIGHBOT+"\n";
        // String s2=VERT+FONT_GIRNO_CANCEL+hello2+VERT+"\n"+LEFTBOT+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+LINE+RIGHBOT+"\n";
        // String strres=strresini+s1+s2;


        //   String beg=INITPRINTER +PAGE_MODE+PRINTAREA1+hello+PRINTAREA2+hello2+LF+PRINT_IN_PAGEMODE
        //           +PAGE_MODE+PRINTAREA1+hello22+PRINTAREA2+hello23+"\n"+PRINT_IN_PAGEMODE
        //           +PAGE_MODE+PRINTAREA1+hello32+PRINTAREA2+hello33+"\n"+PRINT_IN_PAGEMODE;


        //  String beg=INITPRINTER +PAGE_MODE+PRINTAREA1+hello+PRINTAREA2+hello2
        //          +PRINTAREA3+hello22+PRINTAREA4+hello23
        //          +PRINTAREA5+hello32+PRINTAREA6+hello33+"\n"+PRINT_IN_PAGEMODE;


        String firstLine="------------------------------------------------"+"\n";
        String[] splitedString=splitStringEvery(hello,35);
        firstLine=firstLine+splitedString[0]+hello2+"\n";

        for( int i = 1; i < splitedString.length ; i++)
        {
            firstLine =firstLine+ splitedString[i]+"\n";

        }
        firstLine=firstLine+"------------------------------------------------"+"\n";


        String[] splitedString2=splitStringEvery(hello22,35);
        String firstLine2=splitedString2[0]+hello23+"\n";

        for( int i = 1; i < splitedString2.length ; i++)
        {
            firstLine2 =firstLine2+ splitedString2[i]+"\n";

        }
        firstLine2=firstLine2+"------------------------------------------------"+"\n";

        String strres=strresini+firstLine+firstLine2;



        //  String strres=INITPRINTER +PRINTAREAW1+hello+INITPRINTER+LEFTMARGIN+PRINTAREAW1+hello2+"\n";

        // String strres=escStr +"@" +LINESPASE4+str+FONTBIG+"Hello world "+VERT+"ASA \n Привет МИР !!! \n"+FONTSMAL+"Hello "+VERT+" world  ASA \n Привет МИР !!! \n\n"+LEFTPOS+DOUBLE_HW+RAMKA+"Это пишется слева "+VERT+"с переносом на новую строку по идее\n";

        //  byte[] message=convertToBytes("Hello world \n Привет МИР \n");

        //   byte[] c = new byte[escCenter.length + message.length];
        //   System.arraycopy(escCenter, 0, c, 0, escCenter.length);
        //  System.arraycopy(message, 0, c, escCenter.length, message.length);
        // return c;
        //char [] utfbutes=strres.getBytes()

       // return convertToBytes(strres);

        return convertToBytesCPP866(strres);
    }

    public static byte[] convertToBytes(String s){


        //byte[] b = s.getBytes();
        byte[] b = s.getBytes(Charset.forName(CODEPAGE));
        return b;

    }

    public static byte[] convertToBytesCPP866(String s){
        ArrayList<Byte> in = new ArrayList<Byte>();
        int lenS=s.length();

        for (int i = 0; i < lenS; i++) {
            in.add((byte) findIndex(Character.codePointAt(s, i)));
        }

        int n = in.size();
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            out[i] = in.get(i);
        }
        //byte[] b = s.getBytes();
       // byte[] b = s.getBytes(Charset.forName(CODEPAGE));
        return out;

    }


    public void OpenGoods(View v) {
        Intent intentU = new Intent(this, OrdersByUserActivity.class);//OrderWorkActivity.class
        startActivity(intentU);
//        final Handler handler=new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 10) {
//                    txtStatus.setText("PRINTER RESPONSED");
//
//                    //mProgress.setProgress(0);
//                    //mProgress.setVisibility(View.INVISIBLE);
//                    // Toast.makeText(getApplicationContext(),"Загрузка завершена",Toast.LENGTH_SHORT).show();
//                }else if(msg.what == 1){
//                    txtStatus.setText("TEST STARTED...");
//                }else if(msg.what==6){
//                    txtStatus.setText("Printer НЕ ОТВЕЧАЕТ");
//                }else if(msg.what>100){
//                    txtStatus.setText("ERRROR "+String.valueOf(msg.what));
//                }
//            }
//
//
//        };
//
//
//        try {
//            PosPrinter.startTestPrint(Config.glPrinters.get(0), handler);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    public void testden(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
              //  String param = "t;" + currentZal + ";" + String.valueOf(pixelW) + ";" + String.valueOf(pixelH);
                //TODO
             //   Log.d("PARAM", param);
              //  data = Socket1c.writeToSocketAndGet(ServerIP, ServerPort, param);

                Log.d("Socket", data.toString());
                Log.d("LEN", String.valueOf(data.size()));
                Looper.loop();
            }
        }).start();

    }

    public void drawBtn(View v) {

        RelativeLayout r = (RelativeLayout) findViewById(R.id.linearLayout1);
//        int pixel=this.getWindowManager().getDefaultDisplay().getWidth();
//        int pixelH=this.getWindowManager().getDefaultDisplay().getHeight();
//        int dp = (int)(pixel*getResources().getDisplayMetrics().density);
//        int dpH = (int)(pixelH*getResources().getDisplayMetrics().density);
//        Log.d("Pix",String.valueOf(pixel));
//        Log.d("DP",String.valueOf(dp));
//        Log.d("Pix_H",String.valueOf(pixelH));
//        Log.d("DP_H",String.valueOf(dpH));

        View.OnClickListener btnTable = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTableClick(view);
            }
        };

        for (String temp : data) {
            // System.out.println(temp);
            String[] fields = temp.split(";");
            RelativeLayout.LayoutParams layoutParamsTMP = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTMP.setMargins((int) (Integer.parseInt(fields[3])), (int) (Integer.parseInt(fields[2])), 0, 0);

            Button okButtonTMP = new Button(this);
            okButtonTMP.setText(fields[1].substring(fields[1].length() - 2));
            okButtonTMP.setBackgroundResource(R.drawable.button_black);
            okButtonTMP.setTextAppearance(this, R.style.ButtonText);
            okButtonTMP.setPadding(0, 0, 0, 0);
            //есть заказ на столе
            if (fields[5].length() > 1) {
                okButtonTMP.setBackgroundResource(R.drawable.button_yellow);
                okButtonTMP.setPadding(0, 0, 0, 0);
            }
            okButtonTMP.setWidth(65);
            okButtonTMP.setHeight(65);

            Map<String, String> tagObj = new HashMap<String, String>();
            tagObj.put("stol_id", fields[0]);
            tagObj.put("ofic_id", fields[4]);
            okButtonTMP.setTag(tagObj); //TAG - object

            okButtonTMP.setOnClickListener(btnTable);
            //okButtonTMP.setWidth(40);
            //okButtonTMP.setHeight(40);
            // ll2.addView(okButton2, layoutParams2);
            r.addView(okButtonTMP, layoutParamsTMP);
        }
    }

    @SuppressWarnings("unchecked")
    public void btnTableClick(View v) {
        Intent intent = new Intent(this, OrderWorkActivity.class);
        HashMap<String, String> tagObj = (HashMap<String, String>) v.getTag();
        intent.putExtra("ofic_id", tagObj.get("ofic_id"));
        intent.putExtra("zal_id", currentZal);
        intent.putExtra("stol_id", tagObj.get("stol_id")); //TAG - Номер стола

        //intent.putExtra("tabletag",tagObj.get("tbl") ); //TAG - Номер стола
        startActivity(intent);
    }

    public void ExportDocs(View v){
        final Handler handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Exporter.MSG_DATA_DOWNLOADED) {
                    txtStatus.setText("OK.Synced");

                    //mProgress.setProgress(0);
                    //mProgress.setVisibility(View.INVISIBLE);
                    // Toast.makeText(getApplicationContext(),"Загрузка завершена",Toast.LENGTH_SHORT).show();
                } else if (msg.what == Exporter.MSG_NETWORK_ERROR) {
                    txtStatus.setText("MSG_NETWORK_ERROR");
                } else if (msg.what == Exporter.MSG_SERVER_ERROR) {
                    txtStatus.setText("MSG_SERVER_ERROR");
                }else if (msg.what == Exporter.MSG_FILE_ERROR) {
                    txtStatus.setText("MSG_FILE_ERROR");
                }else if (msg.what == Exporter.MSG_NO_DATA) {
                    txtStatus.setText("MSG_NO_DATA");
                }else{
                    txtStatus.setText("NOT FOUND MESS");
                }
            }


        };

        Exporter exporter=new Exporter(handler,0);
        exporter.startExport();
    }

    public void openUpdate(View v) {
        //Intent intentU = new Intent(this, UpdateDB.class);
        //startActivity(intentU);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        String ip="http://"+sharedPref.getString("pref_serverIP","192.168.1.3")+":"+sharedPref.getString("pref_serverPort","8989");
        String catalog=sharedPref.getString("pref_server_basename","demo");
        String pin=sharedPref.getString("pref_server_pin","12341234");;
        final String patFile= this.getFilesDir().getPath() + "/" + "89oficiant/server/in/";
         final Handler handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Downloader.MSG_DATA_DOWNLOADED) {
                    txtStatus.setText("OK.Inserted to DB");
                    insertToDB(patFile);
                    //mProgress.setProgress(0);
                    //mProgress.setVisibility(View.INVISIBLE);
                    // Toast.makeText(getApplicationContext(),"Загрузка завершена",Toast.LENGTH_SHORT).show();
                } else if (msg.what == Downloader.MSG_NETWORK_ERROR) {
                    txtStatus.setText("MSG_NETWORK_ERROR");
                } else if (msg.what == Downloader.MSG_SERVER_ERROR) {
                    txtStatus.setText("MSG_SERVER_ERROR");
                }else if (msg.what == Downloader.MSG_DIRECTORY_ERROR) {

                }else {
                    // importStatus.setText("Загружено... "+String.valueOf(msg.what)+" %");
                }
            }


        };

        Downloader dwnloader=new Downloader(ip,catalog,pin,patFile,handler);
        dwnloader.startDownload();
    };


    public void insertToDB(String pathToFile){
        final Handler handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Importer.MSG_DATA_SYNCED) {
                    txtStatus.setText("Загрузка  Закончена !");
                    //mProgress.setProgress(0);
                    //mProgress.setVisibility(View.INVISIBLE);
                    // Toast.makeText(getApplicationContext(),"Загрузка завершена",Toast.LENGTH_SHORT).show();
                } else if (msg.what == Importer.MSG_DB_ERROR) {
                    txtStatus.setText("Загрузка в базу...");
                } else if (msg.what == Importer.MSG_NEXT_DATA_STARTED) {
                    txtStatus.setText("Начинается загрузка...");
                } else {
                    txtStatus.setText("Загружено... "+String.valueOf(msg.what)+" %");
                }
            }

        };



        Importer importer=new Importer(DBConnector.instance(this).getDB(),pathToFile,"refs.zip",handler);
        importer.startImport();
    }

    public void showPreferencies() {
        Intent i = new Intent(Config.context, Prefs.class);
        startActivity(i);
    }

    public void readPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ServerIP = sharedPref.getString("serverIP", "");
        ServerPort = sharedPref.getString("serverPort", "");
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        readPrefs();
        // Toast.makeText(Config.context,Config.glUserName+"-"+Config.glUserUUID,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.d(LOG_TAG, "selected: position = " + itemPosition + ", id = "
                + itemId + ", " + MenuZals.get(itemPosition));
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tables_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                //newGame();
                return true;
            case R.id.menu_prefs:
                showPreferencies();
                return true;
            case R.id.menu_update:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getGlobalVar() {

    }

    private void setGlobalVar(String varName, String val) {

    }
}
