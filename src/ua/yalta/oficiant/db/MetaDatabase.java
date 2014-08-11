package ua.yalta.oficiant.db;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 29.10.12
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public final class MetaDatabase {
    private MetaDatabase() {
    }

    public static final class Goods implements BaseColumns {
        public static final String TABLE_NAME = "table_goods";
        public static final String CODE = "code";
        public static final String PARENTCODE = "parentcode";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String SKLADCODE = "sklad";
        public static final String PRINTERCODE = "printer";
        public static final String BAZED = "bazedin";

        public static final String GOODSPARENT_IDX_CREATE = "CREATE INDEX parentIdx " +
                "ON " + Goods.TABLE_NAME + " (" + Goods.PARENTCODE + ")";

        public static final String CREATE_TABLE = "CREATE TABLE " + Goods.TABLE_NAME + " ("
                + Goods._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Goods.CODE + " TEXT UNIQUE NOT NULL,"
                + Goods.PARENTCODE + " TEXT,"
                + Goods.NAME + " TEXT,"
                + Goods.PRICE + " REAL DEFAULT 0,"
                + Goods.SKLADCODE + " TEXT,"
                + Goods.PRINTERCODE + " TEXT,"
                + Goods.BAZED + " TEXT"
                + ");";
        private Goods() {
        }
    }

    public static final class Groups implements BaseColumns {
        public static final String TABLE_NAME = "table_groups";
        public static final String CODE = "code";
        public static final String PARENTCODE = "parentcode";
        public static final String NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE " + Groups.TABLE_NAME + " ("
                + Groups._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Groups.CODE + " TEXT UNIQUE NOT NULL,"
                + Groups.PARENTCODE + " TEXT,"
                + Groups.NAME + " TEXT"
                + ");";

        private Groups() {
        }
    }

    public static final class Sklads implements BaseColumns {
        public static final String TABLE_NAME = "table_sklads";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String CREATE_TABLE = "CREATE TABLE " + Sklads.TABLE_NAME + " ("
                + Sklads._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Sklads.CODE + " TEXT UNIQUE NOT NULL,"
                + Sklads.NAME + " TEXT"
                + ");";

        private Sklads() {
        }
    }

    public static final class Printers implements BaseColumns {
        public static final String TABLE_NAME = "table_printers";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String IP = "ip";
        public static final String PORT = "port";

        public static final String CREATE_TABLE = "CREATE TABLE " + Printers.TABLE_NAME + " ("
                + Printers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Printers.CODE + " TEXT UNIQUE NOT NULL,"
                + Printers.NAME + " TEXT,"
                + Printers.IP + " TEXT,"
                + Printers.PORT + " TEXT"
                + ");";

        private Printers() {
        }
    }

    public static final class Zals implements BaseColumns {
        public static final String TABLE_NAME = "table_zals";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String CREATE_TABLE = "CREATE TABLE " + Zals.TABLE_NAME + " ("
                + Zals._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Zals.CODE + " TEXT UNIQUE NOT NULL,"
                + Zals.NAME + " TEXT"
                + ");";

        private Zals() {
        }
    }

    public static final class Modificators implements BaseColumns {
        public static final String TABLE_NAME = "table_modif";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String CREATE_TABLE = "CREATE TABLE " + Modificators.TABLE_NAME + " ("
                + Modificators._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Modificators.CODE + " TEXT UNIQUE NOT NULL,"
                + Modificators.NAME + " TEXT"
                + ");";

        private Modificators() {
        }
    }

    public static final class Vidoplat implements BaseColumns {
        public static final String TABLE_NAME = "table_vidopl";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String CREATE_TABLE = "CREATE TABLE " + Vidoplat.TABLE_NAME + " ("
                + Vidoplat._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Vidoplat.CODE + " TEXT UNIQUE NOT NULL,"
                + Vidoplat.NAME + " TEXT"
                + ");";

        private Vidoplat() {
        }
    }
    public static final class Users implements BaseColumns {
        public static final String TABLE_NAME = "table_users";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String PASSWD = "passwd";
        public static final String ROLE = "role";
        public static final String CREATE_TABLE = "CREATE TABLE " + Users.TABLE_NAME + " ("
                + Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Users.CODE + " TEXT UNIQUE NOT NULL,"
                + Users.NAME + " TEXT,"
                + Users.PASSWD + " TEXT,"
                + Users.ROLE + " TEXT"
                + ");";

        private Users() {
        }
    }

    public static final class Clients implements BaseColumns {
        public static final String TABLE_NAME = "table_clients";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String ISFULLDISKONT = "isfulldisk";
        public static final String DISKONT = "diskont";
        public static final String CREATE_TABLE = "CREATE TABLE " + Clients.TABLE_NAME + " ("
                + Clients._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Clients.CODE + " TEXT UNIQUE NOT NULL,"
                + Clients.NAME + " TEXT,"
                + Clients.ISFULLDISKONT + " INTEGER DEFAULT 0,"
                + Clients.DISKONT + " TEXT"
                + ");";

        private Clients() {
        }
    }


    public static final class Orders implements BaseColumns{
        private Orders(){}

        public static final String TABLE_NAME="table_orders";
        public static final String DATE_DOCS="docs_date";
        public static final String NUM="docs_num";
        public static final String COMMENT="docs_comment";
        public static final String SUM="docs_sum";
        public static final String DISCPERC="docs_persent";
        public static final String KLIENT="docs_klient";
        public static final String USER="docs_user";
        public static final String OPLACHEN="docs_oplachen";
        public static final String VIDOPLAT="docs_vidoplat";
        public static final String PRINTED="printed";
        public static final String SYNCED="docs_synced";
        public static final String ZAL="docs_zal";
        public static final String STOL="docs_stol";

        public static final String SORT_DEF=DATE_DOCS+ " ASC";

        public static final String CREATE_TABLE = "CREATE TABLE " + Orders.TABLE_NAME + " ("
                + Orders._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Orders.DATE_DOCS + " DATETIME,"
                + Orders.NUM + " TEXT,"
                + Orders.COMMENT + " TEXT,"
                + Orders.SUM + " REAL,"
                + Orders.DISCPERC + " REAL DEFAULT 0,"
                + Orders.KLIENT + " TEXT,"
                + Orders.USER + " TEXT,"
                + Orders.OPLACHEN + " INTEGER DEFAULT 0,"
                + Orders.VIDOPLAT + " TEXT,"
                + Orders.SYNCED + " INTEGER DEFAULT 0,"
                + Orders.PRINTED + " INTEGER DEFAULT 0,"
                + Orders.STOL + " TEXT,"
                + Orders.ZAL + " TEXT"
                + ");";

    }


    public static final class OrdersRows implements BaseColumns{
        private OrdersRows(){}

        public static final String TABLE_NAME="table_rowdocs";
        public static final String DOCS_ID="rowdocs_docs_id";
        public static final String GOODS_CODE="row_goods_code";
        public static final String GOODS_NAME="row_goods_name";
        public static final String QTY="rowdocs_qty";
        public static final String QTY_PRINTED="row_qty_printed";
        public static final String PRINTER_CODE="printer";
        public static final String PRICE="rowdocs_price";
        public static final String SUM="rowdocs_sum";
        public static final String DISCOUNT="rowdocs_disk";
        public static final String MOD_NAMES="row_modnames";
        public static final String BAZED = "bazedin";

        public static final String CREATE_TABLE="CREATE TABLE " + OrdersRows.TABLE_NAME+ " ("
                + OrdersRows._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + OrdersRows.DOCS_ID + " INTEGER,"
                + OrdersRows.GOODS_CODE + " TEXT,"
                + OrdersRows.GOODS_NAME + " TEXT,"
                + OrdersRows.QTY + " REAL,"
                + OrdersRows.QTY_PRINTED + " REAL,"
                + OrdersRows.PRINTER_CODE + " TEXT,"
                + OrdersRows.PRICE + " REAL,"
                + OrdersRows.SUM + " REAL,"
                + OrdersRows.MOD_NAMES + " TEXT,"
                + OrdersRows.BAZED + " TEXT,"
                + OrdersRows.DISCOUNT + " REAL DEFAULT 0"
                + ");";

        public static final String IDX_ROW_DOCS_CREATE="CREATE INDEX rowdocsIdx "+
                "ON "+OrdersRows.TABLE_NAME+" ("+OrdersRows.DOCS_ID+")";

    }


    // NE ISPOLZUEM
    public static final class TablesOfZal implements BaseColumns {
        public static final String TABLE_NAME = "tableofzals";
        public static final String NAME = "name";
        public static final String TOP = "top_coord";
        public static final String LEFT = "left_ccord";
        public static final String ZAL_CODE = "zal_code";
        public static final String CREATE_TABLE = "CREATE TABLE " + TablesOfZal.TABLE_NAME + " ("
                + TablesOfZal._ID + " INTEGER PRIMARY KEY ,"
                + TablesOfZal.NAME + " TEXT ,"
                + TablesOfZal.ZAL_CODE + " TEXT ,"
                + TablesOfZal.TOP + " TEXT ,"
                + TablesOfZal.LEFT + " TEXT"
                + ");";
        public static final String TABLEZAL_IDX_CREATE = "CREATE INDEX tablezalIdx " +
                "ON " + TablesOfZal.TABLE_NAME + " (" + TablesOfZal.ZAL_CODE + ")";
        private TablesOfZal() {
        }
    }


}
