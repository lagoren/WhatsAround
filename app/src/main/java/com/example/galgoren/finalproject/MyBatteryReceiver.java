package com.example.galgoren.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by galgoren on 10/2/17.
 */


// battery receiver class
public class MyBatteryReceiver extends BroadcastReceiver {


    private OnBatteryChangedListener listener;

    public MyBatteryReceiver (OnBatteryChangedListener listener){
        this.listener = listener;
    }


    //----------------------------------------------------------------------------------------------


    // checking if phone isPlugged
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        boolean isPlugged = true;


        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            isPlugged = true;
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            isPlugged = false;
        }

        listener.batteryChanged(isPlugged);
    }


    //----------------------------------------------------------------------------------------------


    // interface for battery status
    public interface OnBatteryChangedListener{
        void batteryChanged(boolean isPlugged);
    }
}
