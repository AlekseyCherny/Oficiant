package ua.yalta.oficiant.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 31.10.12
 * Time: 9:17
 * To change this template use File | Settings | File Templates.
 */
public class DateHelper {
    public static String getNowFormatedDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(year - 1900, month, day);
        return dateFormat.format(date);
    }

    public static String formatFromPicker(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(year - 1900, month, day);
        return dateFormat.format(date);
    }
}
