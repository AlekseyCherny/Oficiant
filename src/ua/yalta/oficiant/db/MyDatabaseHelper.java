package ua.yalta.oficiant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ua.yalta.oficiant.db.MetaDatabase.*;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 31.10.12
 * Time: 8:57
 * To change this template use File | Settings | File Templates.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "oficiant.db";
    private static final int DATABASE_VERSION = 2;
    static private MyDatabaseHelper _instance;
    Context mContext;

    protected MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    // ВЫЗОВ ВСЕГДА ЭТОГО МЕТОДА
    public static MyDatabaseHelper instance(Context context) {
        if (_instance == null) {
            _instance = new MyDatabaseHelper(context);
        }
        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Sklads.CREATE_TABLE);
        db.execSQL(Printers.CREATE_TABLE);
        db.execSQL(Groups.CREATE_TABLE);
        db.execSQL(Goods.CREATE_TABLE);
        db.execSQL(Goods.GOODSPARENT_IDX_CREATE);
        db.execSQL(Users.CREATE_TABLE);
        db.execSQL(Zals.CREATE_TABLE);

        db.execSQL(Vidoplat.CREATE_TABLE);
        db.execSQL(Modificators.CREATE_TABLE);

        db.execSQL(Orders.CREATE_TABLE);
        db.execSQL(OrdersRows.CREATE_TABLE);
        db.execSQL(OrdersRows.IDX_ROW_DOCS_CREATE);

        db.execSQL(Clients.CREATE_TABLE);

        db.execSQL(TablesOfZal.CREATE_TABLE);
        db.execSQL(TablesOfZal.TABLEZAL_IDX_CREATE);

        createAdminUser(db);
    }

    private void createAdminUser(SQLiteDatabase db){
        ContentValues dataStr=new ContentValues();
        dataStr.put(Users.NAME,"_admin_");
        dataStr.put(Users.PASSWD,"13.04.");
        dataStr.put(Users.CODE,"-777");
        dataStr.put(Users.ROLE,"SuperAdmin");
        db.insert(Users.TABLE_NAME,null,dataStr);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion==2){
            upgradeTo2(db);
        }

    }

    public void upgradeTo2(SQLiteDatabase db){
        db.beginTransaction();
        try {


            String sqlUp2= "ALTER TABLE " +OrdersRows.TABLE_NAME+" ADD COLUMN "+OrdersRows.MOD_NAMES+" TEXT";
            db.execSQL(sqlUp2);

            String sqlUp1= "ALTER TABLE " +OrdersRows.TABLE_NAME+" ADD COLUMN "+OrdersRows.BAZED+" TEXT";
            db.execSQL(sqlUp1);

            String sqlUp3= "ALTER TABLE " +Orders.TABLE_NAME+" ADD COLUMN "+Orders.STOL+" TEXT";
            db.execSQL(sqlUp3);

            String sqlUp4= "ALTER TABLE " +Orders.TABLE_NAME+" ADD COLUMN "+Orders.PRINTED+" INTEGER DEFAULT 0";
            db.execSQL(sqlUp4);

            db.execSQL(Clients.CREATE_TABLE);

            db.setTransactionSuccessful();



        } finally {
            db.endTransaction();
        }
    }



    public void clearDB(SQLiteDatabase db) {
       // db.execSQL("DROP TABLE IF EXISTS " + Sklads.TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS " + Printers.TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS " + Groups.TABLE_NAME);
       // db.execSQL("DROP INDEX IF EXISTS parentIdx");
       // db.execSQL("DROP TABLE IF EXISTS " + Goods.TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS " + Users.TABLE_NAME);
      //  db.execSQL("DROP TABLE IF EXISTS " + TablesOfZal.TABLE_NAME);

      //  db.execSQL("DROP INDEX IF EXISTS tablezalIdx");
      //  db.execSQL("DROP TABLE IF EXISTS " + Zals.TABLE_NAME);
    }
}
