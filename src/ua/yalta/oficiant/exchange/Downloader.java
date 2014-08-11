package ua.yalta.oficiant.exchange;

import android.os.Handler;
import android.os.Looper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import ua.yalta.oficiant.net.NetUtils;

import java.io.*;


/**
 * Created by aleks on 25.04.14.
 * Загружает данные с сервера в виде Зип архива - распаковывает и загружает в базу данных
 *
 */
public class Downloader {
    //pathToSave= this.getFilesDir().getPath() + "/" + "89Account/server/in/";
    String pathToSave;
    //prefs.getString("excgoodurl","http://192.168.1.3:8989");
    String mURL;
    String namespace;
    String namespace_auth;

    Handler handler;

    public static final int MSG_SERVER_ERROR=404;
    public static final int MSG_DATA_DOWNLOADED=200;
    public static final int MSG_NETWORK_ERROR=333;
    public static final int MSG_DIRECTORY_ERROR=222;

    public Downloader(String serverIpPort,String serverCatalog,String serverPincode, String PathToSaveFile,Handler statusHandler) {

        this.pathToSave=PathToSaveFile;
        this.mURL=serverIpPort;
        this.namespace=serverCatalog;
        this.namespace_auth=serverPincode;
        this.handler=statusHandler;
    }

    public void startDownload(){
        if(!NetUtils.isNetworkAvailable()){
            handler.sendEmptyMessage(MSG_NETWORK_ERROR); //333
            //Toast.makeText(getApplicationContext(), "Сеть недоступна !", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                downloadRefsFromServer();
                Looper.loop();
            }
        } ).start();
    }



    //загрузка файла с сервера
    private void   downloadRefsFromServer(){

        String mFileNameLocal="refs.zip";
        String UrlPath="refs.zip";

        File dirToSave = new File(pathToSave);
        boolean dirExists= dirToSave.mkdirs() || dirToSave.isDirectory();

        if(!dirExists){
            handler.sendEmptyMessage(MSG_DIRECTORY_ERROR);
            return;
        }

        boolean mFileUpdated;
        //String mError;
        try {
            mFileUpdated = true;
            //mError="";

            //String phoneID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);

            //mURL=prefs.getString("excgoodurl","http://192.168.1.3:8989");
            //String namespace=prefs.getString("db_namespace","");
            //String namespace_auth=prefs.getString("db_auth_server","");

            mURL=mURL+"/shop/"+namespace+"/"+ UrlPath;

            // HttpPost post = new HttpPost(mURL);

            HttpGet httpGet=new HttpGet(mURL);

            // прокси //
            //int portOfProxy = android.net.Proxy.getDefaultPort();
            //if (portOfProxy > 0) {
           //     HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(), portOfProxy);
            //    client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            //}

            httpGet.setHeader("PINCODE",namespace_auth);
            httpGet.setHeader("NAMESPACE",namespace);

            HttpResponse response = client.execute(httpGet);

            File fToSave = new File(dirToSave,mFileNameLocal) ;
            FileOutputStream foszip= new FileOutputStream(fToSave);

            InputStream is_reader =new BufferedInputStream(response.getEntity().getContent());

            final byte[] buffer = new byte[8192];
            int count;
            while ((count =is_reader.read(buffer))>=0){
                foszip.write(buffer,0,count);
            }

            foszip.close();
            is_reader.close();


        } catch (org.apache.http.client.ClientProtocolException e) {
            mFileUpdated = false;
            //mError = "ClientProtocolException: " + e.getMessage();
        } catch (IOException e) {
            mFileUpdated = false;
            // mError = "IOException: " + e.getMessage();

        } catch (Exception e) {
            mFileUpdated = false;
            //mError = "Exception: " + e.getMessage();
        }


        if (mFileUpdated){
            handler.sendEmptyMessage(MSG_DATA_DOWNLOADED); //200

        }else{
            handler.sendEmptyMessage(MSG_SERVER_ERROR);//404
        }
    }


  /*  //основная загрузка
    private void ImportRefs(String fName){
        int percent=0;
        long count_f;
        boolean transactionComlete;
        transactionComlete = false;

        // String pathFrom=Environment.getExternalStorageDirectory().getPath()+"/"+"89Account/server/in/" ;
        String pathFrom=pathToSave;// this.getFilesDir().getPath()+"/"+"89Account/server/in/" ;
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

            ExchangeAdapter adapter_loader=new ExchangeAdapter(DBConnector.instance(mContext).getDB());
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
    }*/

   /* private final Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            *//*if (msg.what==404){  //Ошибка Сервера
                importStatus.setText("Ошибка загрузки с сервера !");
                //mProgress.setProgress(0);
                //mProgress.setVisibility(View.INVISIBLE);
                // Toast.makeText(getApplicationContext(),"Загрузка завершена",Toast.LENGTH_SHORT).show();
            } else if (msg.what==MSG_DATA_DOWNLOADED){//200
                importStatus.setText("Данные загружены! Распаковка...");
            } else if (msg.what==201){
                importStatus.setText("Загрузка в базу...");
            } else if (msg.what==199){
                importStatus.setText("Начинается загрузка...");
            }
            else if (msg.what==MSG_DATA_SYNCED){
                importStatus.setText("Обновление завершено !");
                //mProgress.setProgress(0);
                //mProgress.setVisibility(View.INVISIBLE);
                // Toast.makeText(getApplicationContext(),"Выгрузка документов завершена",Toast.LENGTH_SHORT).show();
            } else if (msg.what==MSG_DB_ERROR){
                importStatus.setText("Ошибка при загрузке в базу данных !");
            }
            else  if (msg.what==701){
                importStatus.setText("Грузим ... "+msg.obj.toString());
            }else  if (msg.what==444){
                startExportDocsBtn.setVisibility(View.VISIBLE);
                startExportDocsBtn.setEnabled(true);
            } else{
                importStatus.setText("Загружено... "+String.valueOf(msg.what)+" %");
            }*//*
        }
    };*/


}
