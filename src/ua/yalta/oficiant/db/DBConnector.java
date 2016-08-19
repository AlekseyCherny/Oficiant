package ua.yalta.oficiant.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import ua.yalta.oficiant.db.MetaDatabase.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 31.10.12
 * Time: 9:10
 * To change this template use File | Settings | File Templates.
 */
public class DBConnector {
    static private DBConnector _instance;
    Context mContext;
    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;

    protected DBConnector(Context _context) {
        this.dbHelper = MyDatabaseHelper.instance(_context);
        mContext = _context;
    }

    public static DBConnector instance(Context _context) {
        if (_instance == null) {
            _instance = new DBConnector(_context);
        }
        return _instance;
    }

    public void initDB() {
        if (this.db == null) {
            this.open();
        }
    }

    public SQLiteDatabase getDB(){
        if(this.db==null){
            this.open();
        }
        return db;
    }

    private void open() throws SQLException {
        try {
            this.db = this.dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            //Log.v("Open database exception caught", ex.getMessage());
            this.db = this.dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        this.dbHelper.close();
        //this.db.close();
    }


    public Cursor getRefCodeNameList(String tblName){
        String query = "SELECT "
                + "_id, "
                + "code, "
                + "name "
                + " FROM "
                + tblName
                ;
        return db.rawQuery(query, null);
    }

    /////////////////////////////////////////////////////////////////////////////
    public Cursor getGroupsByParent(String parentCode) {
        String query = "SELECT "
                + Groups._ID + ", "
                + Groups.CODE + ", "
                + Groups.NAME + ", "
                + Groups.PARENTCODE
                + " FROM "
                + Groups.TABLE_NAME
                + " WHERE " + Groups.PARENTCODE + " = '" + parentCode + "'"
                + " ORDER BY "+Groups.NAME;;
        return db.rawQuery(query, null);
    }

    public Cursor getAllGroups() {
        String query = "SELECT  "
                + Groups._ID + ", "
                + Groups.CODE + ", "
                + Groups.NAME + ", "
                + Groups.PARENTCODE
                + " FROM "
                + Groups.TABLE_NAME
                ;
        return db.rawQuery(query, null);
    }


    public Cursor getGoodsByParent(String parentCode) {
       /* String query = "SELECT "
                + Goods._ID + ", "
                + Goods.CODE + ", "
                + Goods.NAME + ", "
                + Goods.PARENTCODE + ", "
                + Goods.PRICE + ", "
                + Goods.BAZED
                + " FROM "
                + Goods.TABLE_NAME
                + " WHERE " + Goods.PARENTCODE + " = '" + parentCode + "'";*/
        String query = "SELECT * "
                + " FROM "
                + Goods.TABLE_NAME
                + " WHERE " + Goods.PARENTCODE + " = '" + parentCode + "'"
                + " ORDER BY "+Goods.NAME;
        return db.rawQuery(query, null);
    }

    public Cursor getGroupByCode(String mCode) {
        String query = "SELECT "
                + Groups._ID + ", "
                + Groups.CODE + ", "
                + Groups.NAME + ", "
                + Groups.PARENTCODE
                + " FROM "
                + Groups.TABLE_NAME
                + " WHERE " + Groups.CODE + " = '" + mCode + "'";
        return db.rawQuery(query, null);
    }


/// ORDERS

    public  Cursor getOrderById(long mID){
        String query = "SELECT * "
                + " FROM "
                + Orders.TABLE_NAME
                + " WHERE " + Orders._ID + " = " + mID + ""
                ;
        return db.rawQuery(query, null);
    }

    public Cursor getOrdersOpenedStatusByUser(String userCode) {
        String query = "SELECT * "
//                + Orders._ID + ", "
//                + Orders.NUM + ", "
//                + Orders.DATE_DOCS + ", "
//                + Orders.KLIENT + ", "
//                + Orders.SUM + ", "
//                + Orders.DISCPERC + ", "
//                + Orders.ZAL + ", "
//                + Orders.COMMENT
                + " FROM "
                + Orders.TABLE_NAME
                + " WHERE " + Orders.USER + " = '" + userCode + "'"
                + " AND " +Orders.OPLACHEN +" = '" + 0 + "'"
                ;
        return db.rawQuery(query, null);
    }

    //for Admin
    public Cursor getOrdersOpenedStatusAll() {
        String query = "SELECT * "
//                + Orders._ID + ", "
//                + Orders.NUM + ", "
//                + Orders.DATE_DOCS + ", "
//                + Orders.KLIENT + ", "
//                + Orders.SUM + ", "
//                + Orders.DISCPERC + ", "
//                + Orders.ZAL + ", "
//                + Orders.COMMENT
                + " FROM "
                + Orders.TABLE_NAME
                + " WHERE " +Orders.OPLACHEN +" = '" + 0 + "'"
                ;
        return db.rawQuery(query, null);
    }

    public long addNewOrder(String userCode,String zalName,String stol){
        long rowId;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues cv=new ContentValues();
        cv.put(Orders.DATE_DOCS,dateFormat.format(date));
        cv.put(Orders.USER,userCode);
        cv.put(Orders.ZAL,zalName);
        cv.put(Orders.STOL,stol);

        rowId=db.insert(Orders.TABLE_NAME,null,cv);
        return rowId;
    }

    public void deleteOrder(long orderId){
        db.beginTransaction();
        try {
            String whereRowDoc = OrdersRows.DOCS_ID + " = ?";
            String[] whereArgsRowDoc = {String.valueOf(orderId)};
            db.delete(OrdersRows.TABLE_NAME, whereRowDoc, whereArgsRowDoc);

            String[] whereArgsDoc = {String.valueOf(orderId)};
            db.delete(Orders.TABLE_NAME, "_ID=?", whereArgsDoc);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void tryingToDeleteOrder(long orderId){
        String client = "Удаление заказа !";
        String vidOplat =  "DELETE";

        String query="UPDATE "
                + Orders.TABLE_NAME
                + " SET "
                + Orders.OPLACHEN+" = '"+6+"'" +","
                + Orders.KLIENT+" = '"+client+"'" +","
                + Orders.VIDOPLAT +" = '"+vidOplat+"'"
                + " WHERE "+Orders._ID+" = "+orderId+""
                ;
        ;
        this.db.execSQL(query);
    }



    public void setOrderSum(long orderId,String sum){
        String query="UPDATE "
                + Orders.TABLE_NAME
                + " SET "
                + Orders.SUM+" = '"+sum+"'"
                + " WHERE "+Orders._ID+" = "+orderId+""
                ;
        ;

        this.db.execSQL(query);
    }

    public void setOrderPrinted(long orderId){
        String query="UPDATE "
                + Orders.TABLE_NAME
                + " SET "
                + Orders.PRINTED+" = '"+1+"'"
                + " WHERE "+Orders._ID+" = "+orderId+""
                ;
        ;

        this.db.execSQL(query);
    }

    public void payOrder(long orderId,String vidOplat,String client){
        String query="UPDATE "
                + Orders.TABLE_NAME
                + " SET "
                + Orders.OPLACHEN+" = '"+1+"'" +","
                + Orders.KLIENT+" = '"+client+"'" +","
                + Orders.VIDOPLAT +" = '"+vidOplat+"'"
                + " WHERE "+Orders._ID+" = "+orderId+""
                ;
        ;
        this.db.execSQL(query);
    }

    public Cursor getOrderRowsById(long mId){
        String query = "SELECT * "
        //        + OrdersRows._ID + ", "
        //        + OrdersRows.GOODS_CODE + ", "
        //        + OrdersRows.GOODS_NAME + ", "
        //        + OrdersRows.QTY + ", "
        //        + OrdersRows.PRICE + ", "
        //        + OrdersRows.SUM + ", "
        //        + OrdersRows.DISCOUNT + ", "
       //         + OrdersRows.DOCS_ID
                + " FROM "
                + OrdersRows.TABLE_NAME
                + " WHERE " + OrdersRows.DOCS_ID + " = '" + mId + "'"
        //        + " AND " +Orders.OPLACHEN +" = '" + 0 + "'"
                ;
        return db.rawQuery(query, null);
    }


    public void addRowToOrder(Intent data,long orderID){
        ContentValues dataStr=new ContentValues();
        dataStr.put(OrdersRows.DOCS_ID,orderID);
        dataStr.put(OrdersRows.GOODS_CODE,data.getStringExtra(OrdersRows.GOODS_CODE));
        dataStr.put(OrdersRows.GOODS_NAME,data.getStringExtra(OrdersRows.GOODS_NAME));
        dataStr.put(OrdersRows.QTY,data.getStringExtra(OrdersRows.QTY));
        dataStr.put(OrdersRows.QTY_PRINTED,"0"); // default printed
        dataStr.put(OrdersRows.PRINTER_CODE,data.getStringExtra(OrdersRows.PRINTER_CODE));
        dataStr.put(OrdersRows.PRICE,data.getStringExtra(OrdersRows.PRICE));
        dataStr.put(OrdersRows.SUM,data.getStringExtra(OrdersRows.SUM));
        dataStr.put(OrdersRows.DISCOUNT,data.getStringExtra(OrdersRows.DISCOUNT));
        dataStr.put(OrdersRows.BAZED,data.getStringExtra(OrdersRows.BAZED));
       // if(thisId==0){ //insert
            this.db.insert(OrdersRows.TABLE_NAME,null,dataStr);
      //  }   else{           //update
       //     dataStr.put(MetaDatabase.RegistrDopReks._ID,thisId);
       //     this.db.replace(MetaDatabase.RegistrDopReks.RegistrDopReks_TABLE,null,dataStr) ;
       // }
    }

    public void changeRowOrder(Intent data,long orderID){
        long rowID=data.getLongExtra(OrdersRows._ID,0);
        String qty=data.getStringExtra(OrdersRows.QTY);
        String sum=data.getStringExtra(OrdersRows.SUM);
        String query="UPDATE "
                + OrdersRows.TABLE_NAME
                + " SET "
                + OrdersRows.QTY+" = '"+qty+"'" +","
                + OrdersRows.SUM +" = '"+sum+"'"
                + " WHERE "+OrdersRows.DOCS_ID+" = "+orderID+""
                + " AND "+OrdersRows._ID+" = "+rowID;
                ;
        this.db.execSQL(query);
    }

    public void updateRowModificators(long rowID,long orderID,String mModificators){

        String query="UPDATE "
                + OrdersRows.TABLE_NAME
                + " SET "
                + OrdersRows.MOD_NAMES+" = '"+mModificators+"'"
                + " WHERE "+OrdersRows.DOCS_ID+" = "+orderID+""
                + " AND "+OrdersRows._ID+" = "+rowID;
        ;
        this.db.execSQL(query);
    }

    public void deleteRowOrder(long rowID,long orderId){
        String where=OrdersRows.DOCS_ID +  " = ? AND "+OrdersRows._ID+" = ? ";
        String[] whereArgs={String.valueOf(orderId),String.valueOf(rowID)};
        this.db.delete(OrdersRows.TABLE_NAME,where,whereArgs);
    }

    public void updateRowsOrderAfterPrint(HashMap<Long,String> data,long orderID){
        boolean res = true;
        this.db.beginTransaction();

        for (HashMap.Entry<Long, String> entry : data.entrySet()) {
            long rowID = entry.getKey();
            String qty = entry.getValue();

            String query="UPDATE "
                    + OrdersRows.TABLE_NAME
                    + " SET "
                    + OrdersRows.QTY_PRINTED+" = '"+qty+"'"
                    + " WHERE "+OrdersRows.DOCS_ID+" = "+orderID+""
                    + " AND "+OrdersRows._ID+" = "+rowID;
            ;

            try {
                this.db.execSQL(query);
            } catch (SQLException e) {
                res = false;
                this.db.endTransaction();    //  завершим транзакцию
                break;
            }
        }

        if (res) {
            this.db.setTransactionSuccessful(); // всё прошло успешно
            this.db.endTransaction(); // и завершаем транзакцию
        }
    }


    public Cursor getDocsListNotUploadedToServer(){
        String query="SELECT * "
                + " FROM "
                + Orders.TABLE_NAME
                + " WHERE "+Orders.OPLACHEN +" <> 0 AND "+Orders.SYNCED+" = 0 "
                ;
        return db.rawQuery(query,null);
    }


    public void updDocUploadedOnServer(long[] orderIds){
        String filtrIN=getFiltrIN(orderIds);
        String query="UPDATE "
                + Orders.TABLE_NAME
                + " SET "
                + Orders.SYNCED +" = '"+1+"'"
                + " WHERE "+Orders._ID+" IN "+filtrIN+""
                ;
        ;
        this.db.execSQL(query);
    }

    ///ORDERS END

  private String getFiltrIN(long[] longsIds){
      String res="(";
      for (long i:longsIds){
          res=res+String.valueOf(i)+",";
      }
      res=res.substring(0,res.length()-1)+")";
      return res;
  }


    public Cursor getUserByPass(String passwd) {
        String query = "SELECT "
                + Users._ID + ", "
                + Users.CODE + ", "
                + Users.NAME + ", "
                + Users.ROLE
                + " FROM "
                + Users.TABLE_NAME
                + " WHERE " + Users.PASSWD + " = '" + passwd + "'";
        return db.rawQuery(query, null);
    }

    public Cursor getZals() {
        String query = "SELECT * "
                + " FROM "
                + Zals.TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public Cursor getTablesByZalCode(String zalCode) {
        String query = "SELECT * "
                + " FROM "
                + TablesOfZal.TABLE_NAME
                + " WHERE " + TablesOfZal.ZAL_CODE + " = '" + zalCode + "'"
                ;
        return db.rawQuery(query, null);
    }

    public Cursor getPrinters() {
        String query = "SELECT * "
                + " FROM "
                + Printers.TABLE_NAME;
        return db.rawQuery(query, null);
    }
    public Cursor getUsers() {
        String query = "SELECT * "
                + " FROM "
                + Users.TABLE_NAME;
        return db.rawQuery(query, null);
    }
    public Cursor getClients() {
        String query = "SELECT * "
                + " FROM "
                + Clients.TABLE_NAME;
        return db.rawQuery(query, null);
    }
    public Cursor getClientsFullDiskont() {
        String query = "SELECT * "
                + " FROM "
                + Clients.TABLE_NAME
                + " WHERE " + Clients.ISFULLDISKONT + " = '" + 1 + "'";
        return db.rawQuery(query, null);
    }

    ///////////UPDATE TRANSACTION
    public boolean updateGroupsTrans(List<String> dataList) {
        if (this.db == null) {
            this.open();
        }
        boolean res = true;
        this.db.beginTransaction();

        for (String curstr : dataList) {
            String[] dataSrt = curstr.split(";");
            String goodsName = dataSrt[1];
            goodsName = goodsName.replace("'", "''");

            String sql = "REPLACE INTO " + Groups.TABLE_NAME
                    + " (" + Groups.CODE + ", " + Groups.NAME + ", " + Groups.PARENTCODE + ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + goodsName + "', '"
                    + dataSrt[2]
                    + "')";
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                res = false;
                db.endTransaction();    //  завершим транзакцию
                break;
            }
        }
        if (res) {
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    public boolean updateGoodsTrans(List<String> dataList) {
        if (this.db == null) {
            this.open();
        }
        boolean res = true;
        this.db.beginTransaction();

        for (String curstr : dataList) {
            String[] dataSrt = curstr.split(";");
            String goodsName = dataSrt[1];
            goodsName = goodsName.replace("'", "''");

            String sql = "REPLACE INTO " + Goods.TABLE_NAME
                    + " (" + Goods.CODE + ", " + Goods.NAME + ", " + Goods.PARENTCODE + ", " + Goods.PRICE + ", " + Goods.BAZED + ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + goodsName + "', '"
                    + dataSrt[2] + "', '"
                    + dataSrt[3] + "', '"
                    + dataSrt[4]
                    + "')";
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                res = false;
                db.endTransaction();    //  завершим транзакцию
                break;
            }
        }
        if (res) {
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    public boolean updateUsersTrans(List<String> dataList) {
        if (this.db == null) {
            this.open();
        }
        boolean res = true;
        this.db.beginTransaction();

        for (String curstr : dataList) {
            String[] dataSrt = curstr.split(";");
            String usersName = dataSrt[1];
            usersName = usersName.replace("'", "''");

            String sql = "REPLACE INTO " + Users.TABLE_NAME
                    + " (" + Users.CODE + ", " + Users.NAME + ", " + Users.PASSWD + ", " + Users.ROLE + ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + usersName + "', '"
                    + dataSrt[2] + "', '"
                    + dataSrt[3]
                    + "')";
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                res = false;
                db.endTransaction();    //  завершим транзакцию
                break;
            }
        }
        if (res) {
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }

    public boolean updateZalsTrans(List<String> dataList) {
        if (this.db == null) {
            this.open();
        }
        boolean res = true;
        this.db.beginTransaction();

        for (String curstr : dataList) {
            String[] dataSrt = curstr.split(";");
            String zalName = dataSrt[1];
            zalName = zalName.replace("'", "''");

            String sql = "REPLACE INTO " + Zals.TABLE_NAME
                    + " (" + Zals.CODE + ", " + Zals.NAME + ") "
                    + "VALUES ('" + dataSrt[0] + "', '"
                    + zalName
                    + "')";
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                res = false;
                db.endTransaction();    //  завершим транзакцию
                break;
            }
        }
        if (res) {
            db.setTransactionSuccessful(); // всё прошло успешно
            db.endTransaction(); // и завершаем транзакцию
        }
        return res;
    }
}
