package ua.yalta.oficiant.exchange;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.os.Message;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.utils.ZipDecompress;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

/**
 * Created by aleks on 25.04.14.
 */
public class Importer {
    public static final String F_USERS="users.txt";
    public static final String F_GOODS="goods.txt";
    public static final String F_GROUPS="groups.txt";
    public static final String F_SKLADS="sklads.txt";
    public static final String F_PRINTERS="printers.txt";
    public static final String F_ZALS="zals.txt";
    public static final String F_MODIF="modific.txt";
    public static final String F_VIDOPLAT="vidoplat.txt";
    public static final String F_CLIENTS="clients.txt";

    public static final int MSG_DB_ERROR=105;
    public static final int MSG_DATA_SYNCED=999;
    public static final int MSG_NEXT_DATA_STARTED=701;

    SQLiteDatabase db;
    String pathToZipFile;
    String zipFile;
    Handler handler;


    public Importer(SQLiteDatabase mDb,String PathToZipFile,String mZipFile,Handler handler) {
        this.db=mDb;
        this.pathToZipFile=PathToZipFile;
        this.zipFile=mZipFile;
        this.handler=handler;
    }


    public void startImport(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                decompresAndLoadToDb();
                Looper.loop();
            }
        } ).start();
    }


    private void decompresAndLoadToDb(){
        File dirToSave = new File(pathToZipFile);
        dirToSave.mkdirs();

        //handler.sendEmptyMessage(MSG_DATA_DOWNLOADED); //200
        ZipDecompress d = new ZipDecompress(pathToZipFile+zipFile, pathToZipFile);
        d.unzip();
        //mHandlerImportBase.post(runImportFromServer);

        //получим список распакованных файлов
        File fList[]=dirToSave.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        for (File inListFile:fList){
            Message message=new Message();
            message.what=MSG_NEXT_DATA_STARTED;
            message.obj=inListFile.getName();

            handler.sendMessage(message);

            ImportRefs(inListFile.getName());

        }

        for (File inListFileDel:fList){
            inListFileDel.delete();
        }
        File zipF = new File(dirToSave,zipFile) ;
        zipF.delete();

        DaoUpdater.updatePrinters();

        handler.sendEmptyMessage(MSG_DATA_SYNCED);//999
    }

    private void ImportRefs(String fName){
        int percent=0;
        long count_f;
        boolean transactionComlete;
        transactionComlete = false;

        // String pathFrom=Environment.getExternalStorageDirectory().getPath()+"/"+"89Account/server/in/" ;
        String pathFrom=pathToZipFile;// this.getFilesDir().getPath()+"/"+"89Account/server/in/" ;
        File dir=new File(pathFrom);

        String mFileName=fName;


        try {
            int counter=0;
            int totalDownloaded=0;
            List<String[]> dataList=new ArrayList<String[]>();


            if(!dir.exists()){
                //Toast.makeText(this, "Каталог 89Account/server/in/ не существует на карте", Toast.LENGTH_LONG).show();

            }
            File f = new File(dir,mFileName);
            if(!f.exists()){
                //Toast.makeText(this,"файл "+mFileName+" не найден в папке 89Account/server/in/",Toast.LENGTH_LONG).show();
                return;
            }

            ExchangeAdapter adapter_loader=new ExchangeAdapter(db);
            FileInputStream fileIS = new FileInputStream(f);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
            String readString;

            readString = buf.readLine(); //1-line-comments
            if(readString!= null)  {
                readString = buf.readLine(); //2-line-total
            } else{
                handler.sendEmptyMessage(201);
                fileIS.close();
                return;
            }
            if(readString!= null)  {

                try{
                    count_f=Long.parseLong(readString.trim());
                }catch (NumberFormatException ex) {
                    count_f=1000;//для отработки индикатора
                }
            }else {
                handler.sendEmptyMessage(201);
                fileIS.close();
                return;
            }
            while ((readString = buf.readLine()) != null) {
                String [] item=readString.split(";");
                dataList.add(item);
                counter++;  totalDownloaded++;
                if(counter==500){
                    if(mFileName.equals(F_ZALS)){
                        transactionComlete=adapter_loader.insertZals(dataList);
                    }else if(mFileName.equals(F_SKLADS)){
                        transactionComlete=adapter_loader.insertSklads(dataList);
                    }else if(mFileName.equals(F_GOODS)){
                        transactionComlete=adapter_loader.insertGoods(dataList);
                    }else if(mFileName.equals(F_GROUPS)){
                        transactionComlete=adapter_loader.insertGroups(dataList);
                    } else if(mFileName.equals(F_PRINTERS)){
                        transactionComlete=adapter_loader.insertPrinters(dataList);
                    }else if(mFileName.equals(F_MODIF)){
                        transactionComlete=adapter_loader.insertModif(dataList);
                    }else if(mFileName.equals(F_VIDOPLAT)){
                        transactionComlete=adapter_loader.insertVidoplat(dataList);
                    }else if(mFileName.equals(F_USERS)){
                        transactionComlete=adapter_loader.insertUsers(dataList);
                    }else if(mFileName.equals(F_CLIENTS)){
                        transactionComlete=adapter_loader.insertClients(dataList);
                    }
//                    if(transactionComlete){
//                        Log.d ("DOWNLOAD: ","Загружено "+Long.toString(totalDownloaded)+" из "+Long.toString(count_f));
//                    }else{
//                        Log.d("DOWNLOAD: ","ERROR TRANSACTION");
//                    }
                    counter=0;
                    dataList.clear();

                }
                percent=(int)(((float)totalDownloaded/(float)count_f)*100);
                if(percent>100){
                    percent=99;
                }
                handler.sendEmptyMessage(percent);
            }
            if(counter>0){
                if(mFileName.equals(F_ZALS)){
                    transactionComlete=adapter_loader.insertZals(dataList);
                }else if(mFileName.equals(F_SKLADS)){
                    transactionComlete=adapter_loader.insertSklads(dataList);
                } else if(mFileName.equals(F_GOODS)){
                    transactionComlete=adapter_loader.insertGoods(dataList) ;
                } else if(mFileName.equals(F_GROUPS)){
                    transactionComlete=adapter_loader.insertGroups(dataList);
                } else if(mFileName.equals(F_PRINTERS)){
                    transactionComlete=adapter_loader.insertPrinters(dataList);
                }else if(mFileName.equals(F_MODIF)){
                    transactionComlete=adapter_loader.insertModif(dataList);
                }else if(mFileName.equals(F_VIDOPLAT)){
                    transactionComlete=adapter_loader.insertVidoplat(dataList);
                }else if(mFileName.equals(F_USERS)){
                    transactionComlete=adapter_loader.insertUsers(dataList);
                }else if(mFileName.equals(F_CLIENTS)){
                    transactionComlete=adapter_loader.insertClients(dataList);
                }
//                if(transactionComlete){
//                    //output="Загружены ВСЕ "+Long.toString(totalcounter)+" из "+Long.toString(count_f);
//                }else{
//                    //output="Ошибка загрузки ";
//                }
                dataList.clear();
            }
            handler.sendEmptyMessage(201);

            fileIS.close();

        } catch (FileNotFoundException e) {
            handler.sendEmptyMessage(MSG_DB_ERROR);
//            Toast.makeText(this,"файл import.txt не найден в корне SD",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
//            e.printStackTrace();
            handler.sendEmptyMessage(MSG_DB_ERROR);
        }
        //Looper.myLooper().quit();
    }




}
