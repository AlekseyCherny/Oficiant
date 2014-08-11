package ua.yalta.oficiant.exchange;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import ua.yalta.oficiant.Config;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.net.NetUtils;
import ua.yalta.oficiant.utils.ZipCompress;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aleks on 12.05.14.
 */
public class Exporter {
    public static final int MSG_SERVER_ERROR=404;
    public static final int MSG_DATA_DOWNLOADED=200;
    public static final int MSG_NETWORK_ERROR=333;
    public static final int MSG_FILE_ERROR=222;
    public static final int MSG_NO_DATA=111;

   // SQLiteDatabase db;
   // String pathToZipFile;
   // String zipFile;
    Handler handler;
    long docID;
    public Exporter(Handler handler,long mDocId) {
       // this.db=mDb;
       // this.pathToZipFile=PathToZipFile;
      //  this.zipFile=mZipFile;
        this.handler=handler;
        this.docID=mDocId;
    }

    public void startExport(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                readFromDbAndZip();
                Looper.loop();
            }
        } ).start();
    }


    private void readFromDbAndZip(){
        if(!NetUtils.isNetworkAvailable()){
            if(!(handler==null))
               handler.sendEmptyMessage(MSG_NETWORK_ERROR);
            return;
        }


        String path= Config.context.getFilesDir().getPath()+"/"+"89oficiant/server/out/";
        String prefix= PreferenceManager.getDefaultSharedPreferences(Config.context).getString("pref_server_prefix","");
        if(prefix==null||prefix.length()!=2){
            Toast.makeText(Config.context,"Не верно установлены 2 буквы префикса в настройках",Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String sDateNow=dateFormat.format(date);

        File dir=new File(path);
        dir.mkdirs();
        if(!dir.exists()){
            if(!(handler==null))
              handler.sendEmptyMessage(MSG_FILE_ERROR);
            //Toast.makeText(this, "Каталог 89Account/server/out не существует на карте", Toast.LENGTH_LONG).show();
            return;
        }
        String eol = System.getProperty("line.separator");

        int totalDownloaded=0;
        Cursor mCursor= DBConnector.instance(Config.context).getDocsListNotUploadedToServer();
        long count_f=mCursor.getCount();
        if(count_f<=0){
            //Toast.makeText(this, "Нет данных для выгрузки на сервер", Toast.LENGTH_LONG).show();
            if(!(handler==null))
              handler.sendEmptyMessage(MSG_NO_DATA);
            return;
        }

        final String onlyFileName= sDateNow+prefix;
        final String zipFileName=path+sDateNow+prefix+".zip";

        String[] filesToZip=new String[(int) count_f];
        //массив ID документов
        final long [] docIds=new long[(int)count_f];

        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {


            try{
                long docId=mCursor.getLong(mCursor.getColumnIndex(MetaDatabase.Orders._ID));
                String mDate=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.DATE_DOCS));
                String mZal=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.ZAL));
                String mUser=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.USER));
                String mVidOpl=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.VIDOPLAT));
                String mComment=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.COMMENT));
                String mClient=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.KLIENT));
                String mSum=mCursor.getString( mCursor.getColumnIndex(MetaDatabase.Orders.SUM));

                String docTag=String.valueOf(docId)+"_"+mDate;

                String docFullName=path+docTag+".txt";
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(docFullName), "UTF-8"));

                writer.write("Date;ID;Сумма;Зал;Официант;Вид оплаты;Коммент;Клиент"+eol);
                writer.write(mDate+";"+String.valueOf(docId)+";"+mSum+";"+mZal+";"+mUser+";"+mVidOpl+";"+mComment+";"+mClient+ eol);

                writer.write(eol);
                writer.write("Код товара;Товар;Количество;Цена;Сумма;Скидка"+eol);

                Cursor mCursorRow=DBConnector.instance(Config.context).getOrderRowsById(docId);
                mCursorRow.moveToFirst();
                while (!mCursorRow.isAfterLast()){
                    String s1=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.GOODS_CODE));
                    String s2=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.GOODS_NAME));
                    String s3=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.QTY));
                    String s4=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.PRICE));
                    String s5=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.SUM));
                    String s6=mCursorRow.getString(mCursorRow.getColumnIndex(MetaDatabase.OrdersRows.DISCOUNT));

                    writer.write(s1+";"+s2+";"+s3+";"+s4+";"+s5+";"+s6 + eol);
                    mCursorRow.moveToNext();
                }
                writer.close();
                mCursorRow.close();

                filesToZip[totalDownloaded]= docFullName;
                docIds[totalDownloaded]=docId;

                totalDownloaded++;
            }catch (Exception e){
                if(!(handler==null))
                   handler.sendEmptyMessage(MSG_FILE_ERROR);
                e.printStackTrace();
                if (!mCursor.isClosed()){
                    mCursor.close();
                }
                return;
            }

            mCursor.moveToNext();
        }
        mCursor.close();

        ZipCompress zipCompress=new ZipCompress(filesToZip,zipFileName);
        zipCompress.zip();

        //удаляем текстовые файлы
        for(String f:filesToZip){
            new File(f).delete();
        }

        PostZipData(zipFileName,onlyFileName,docIds);
        Log.d("UPLOAD","File zipped");
    }


    //Отправка документов на сервер
    private void PostZipData(String fullPostFileName,String lastUri,long[] idsToUpdate){



        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Config.context);
        String mURL="http://"+sharedPref.getString("pref_serverIP","192.168.1.3")+":"+sharedPref.getString("pref_serverPort","8989");
        String namespace=sharedPref.getString("pref_server_basename","demo");
        String namespace_auth=sharedPref.getString("pref_server_pin","12341234");

        mURL=mURL+"/shop/"+namespace+"/"+ lastUri;


        File file = new File(fullPostFileName);
        try {

            HttpPost httppost = new HttpPost(mURL);
            httppost.setHeader("PINCODE",namespace_auth);


            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            // прокси //
           // int portOfProxy = android.net.Proxy.getDefaultPort();
            //if (portOfProxy > 0) {
           //     HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(), portOfProxy);
           //     httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
          //  }

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            //reqEntity.setChunked(true); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line ;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }

            //Log.d("RESPONSE",sb.toString());

            if(sb.toString().contains("SERVER OK")){
                if(!(handler==null))
                   handler.sendEmptyMessage(MSG_DATA_DOWNLOADED);
                //TODO update after upload
                DBConnector.instance(Config.context).updDocUploadedOnServer(idsToUpdate);

               // if(updateDb){
               //     mHandlerServerResponse.post(serverResponseOk);
               // }   else{
               //     mHandlerServerResponse.post(UpdateDBError);
               // }

            }else{
                if(!(handler==null))
                   handler.sendEmptyMessage(MSG_SERVER_ERROR);
            }

        } catch (ClientProtocolException e){
            Log.d("RESPONSE ERROR", e.toString());
            if(!(handler==null))
                handler.sendEmptyMessage(MSG_SERVER_ERROR);
        } catch (Exception e) {
            Log.d("RESPONSE ERROR",e.toString());
            if(!(handler==null))
               handler.sendEmptyMessage(MSG_SERVER_ERROR);
        }finally {
            file.delete();
        }


    }

}
