package ua.yalta.oficiant.exchange;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;
import ua.yalta.oficiant.db.MetaDatabase.*;
import ua.yalta.oficiant.db.MetaDatabase.Zals;
import ua.yalta.oficiant.refs.*;

import java.util.List;

/**
 * Created by aleks on 25.04.14.
 */
public class ExchangeAdapter {
    private SQLiteDatabase db;

    public ExchangeAdapter(SQLiteDatabase _db) {
        this.db=_db;
    }

    //0-code,1-parentcode,2-name,
    public boolean insertGroups(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<3){
                continue;
            }
            String Name=dataSrt[2];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Groups.TABLE_NAME
                    +" ("+Groups.CODE+", "+Groups.PARENTCODE+", "+Groups.NAME+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + dataSrt[1]+ "', '"
                    + Name +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    //0-code,1-parentcode,2-name,3-price,4-Skladcode,5-Printercode,6-Edinica
    public boolean insertGoods(List<String[]> dataList){
        Log.d("GOODS TOTAL","Total="+String.valueOf(dataList.size()));

        boolean res=true;

        int inserted=0;
        int skiped=0;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<7){
                skiped++;
                continue;
            }
            String Name=dataSrt[2];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Goods.TABLE_NAME
                    +" ("+Goods.CODE+", "+Goods.PARENTCODE+", "+Goods.NAME+", "+Goods.PRICE+", "+Goods.SKLADCODE+", "+Goods.PRINTERCODE+", "+Goods.BAZED+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + dataSrt[1]+ "', '"
                    + Name+ "', '"
                    + dataSrt[3]+ "', '"
                    + dataSrt[4]+ "', '"
                    + dataSrt[5]+ "', '"
                    + dataSrt[6] +"'"
                    + ")";

            try {
                db.execSQL(sql);
                inserted++;
            }
            catch (SQLException e) {
                res = false;
                Log.d("GOODS ERRR",e.toString());
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }

        Log.d("GOODS","INS="+String.valueOf(inserted)+" SK="+String.valueOf(skiped));
        return res;

    }

    //0-code,1-name,
    public boolean insertSklads(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<2){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Sklads.TABLE_NAME
                    +" ("+Sklads.CODE+", "+Sklads.NAME+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    //0-code,1-name,2-IP,3-port
    public boolean insertPrinters(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<2){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Printers.TABLE_NAME
                    +" ("+Printers.CODE+", "+Printers.NAME+", "+Printers.IP+", "+Printers.PORT+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name+ "', '"
                    + dataSrt[2]+ "', '"
                    + dataSrt[3] +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    //0-code,1-name,
    public boolean insertZals(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<2){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Zals.TABLE_NAME
                    +" ("+Zals.CODE+", "+Zals.NAME+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    //0-code,1-name,
    public boolean insertModif(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<2){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Modificators.TABLE_NAME
                    +" ("+Modificators.CODE+", "+Modificators.NAME+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    public boolean insertVidoplat(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<2){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Vidoplat.TABLE_NAME
                    +" ("+Vidoplat.CODE+", "+Vidoplat.NAME+") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }
    public boolean insertUsers(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<3){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Users.TABLE_NAME
                    +" ("+Users.CODE+", "+Users.NAME+", "+Users.PASSWD+ ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"', '"
                    + dataSrt[2] +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    public boolean insertClients(List<String[]> dataList){
        boolean res=true;

        db.beginTransaction();

        for (String[] dataSrt : dataList) {
            int dataSize=dataSrt.length;

            if (dataSize<4){
                continue;
            }
            String Name=dataSrt[1];
            Name=Name.replace("'","''");

            String sql="";
            sql  = "INSERT OR REPLACE INTO "+ Clients.TABLE_NAME
                    +" ("+Clients.CODE+", "+Clients.NAME+", "+Clients.ISFULLDISKONT+", "+Clients.DISKONT+ ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + Name +"', '"
                    + dataSrt[2] +"', '"
                    + dataSrt[3] +"'"
                    + ")";

            try {
                db.execSQL(sql);
            }
            catch (SQLException e) {
                res = false;
                db.endTransaction();	//  завершим транзакцию
                break;
            }
        }
        if(res){
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }
}
