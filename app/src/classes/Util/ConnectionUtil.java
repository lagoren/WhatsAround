package com.example.galgoren.finalproject.Util;

/**
 * Created by galgoren on 10/15/17.
 */


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



// checking if we got internet connection
public class ConnectionUtil {

    public static boolean isOnline(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if ((netInfo != null) && netInfo.isAvailable() && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }
}


