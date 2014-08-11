package ua.yalta.oficiant.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import ua.yalta.oficiant.Config;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 29.10.12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class NetUtils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) Config.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
