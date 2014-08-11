package ua.yalta.oficiant.utils;

import android.os.Environment;
import android.widget.Toast;
import ua.yalta.oficiant.Config;
import ua.yalta.oficiant.db.DBConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by aleks on 12.07.14.
 */
public class Backup {

    public static final String DB_PATH=DBConnector.instance(Config.context.getApplicationContext()).getDB().getPath();
    public static final String DB_BACK_DIR=Config.context.getFilesDir().getPath() + "/" + "89oficiant/Backup/";
    public static final String DB_BACK_FILE="backup.db";

    public static File BackUpDbStat(){

        File dbFile=new File(DB_PATH);
        File dir = new File(DB_BACK_DIR);
        dir.mkdirs();
        final File backupFile = new File(dir, DB_BACK_FILE);
        boolean res=true;
        try {
            backupFile.createNewFile();
            copyFile(dbFile, backupFile);
            //Toast.makeText(this,"База успешно сохранена в каталог 89Account/Backup/",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            res=false;
            e.printStackTrace();
        }
        if(res){
            return  backupFile;
        } else{
            return null;
        }
    }




    public void restoreDb(){
        File dbFile=new File(DB_PATH);
        File dir = new File(DB_BACK_DIR);
        dir.mkdirs();
        File restoreFile = new File(dir, DB_BACK_FILE);
        if (!restoreFile.exists()) {
            //Toast.makeText(this, "Файл /89Account/Backup/backup.db не существует !", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            dbFile.createNewFile();
            copyFile(restoreFile, dbFile);
           // Toast.makeText(this,"База успешно восстановлена",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
           // Toast.makeText(this,"Ошибка восстановления базы!",Toast.LENGTH_SHORT).show();
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
