package ua.yalta.oficiant.exchange;

import android.database.Cursor;
import ua.yalta.oficiant.Config;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.refs.DaoPrinter;
import ua.yalta.oficiant.refs.DaoUsers;

/**
 * Created by aleks on 02.05.14.
 */
public class DaoUpdater {

    public static void updatePrinters(){
        Cursor cursor=DBConnector.instance(Config.context).getPrinters();
        Config.glPrinters.clear();

        while (cursor.moveToNext()) {
            DaoPrinter dp=new DaoPrinter();
            dp.setCode(cursor.getString(cursor.getColumnIndex(MetaDatabase.Printers.CODE)));
            dp.setNpname(cursor.getString(cursor.getColumnIndex(MetaDatabase.Printers.NAME)));
            dp.setIp(cursor.getString(cursor.getColumnIndex(MetaDatabase.Printers.IP)));
            dp.setPort(cursor.getString(cursor.getColumnIndex(MetaDatabase.Printers.PORT)));

            Config.glPrinters.add(dp);
        }

        cursor.close();
    }

    public static void updateUsers(){
        Cursor cursor=DBConnector.instance(Config.context).getUsers();
        Config.glUsers.clear();
        while (cursor.moveToNext()) {
            DaoUsers user=new DaoUsers();
            user.setCode(cursor.getString(cursor.getColumnIndex(MetaDatabase.Users.CODE)));
            user.setName(cursor.getString(cursor.getColumnIndex(MetaDatabase.Users.NAME)));
            Config.glUsers.add(user);
        }
        cursor.close();

    }
}
