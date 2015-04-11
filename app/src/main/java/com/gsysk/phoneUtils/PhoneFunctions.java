package com.gsysk.phoneUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lenovo on 02-03-2015.
 */
public class PhoneFunctions {

    Activity curActivity = null;

    public PhoneFunctions(Activity activity) {
        curActivity = activity;
    }

    public void makePhoneCall(String[] phoneNumArray, int position) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumArray[position].split(":")[1]));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        curActivity.startActivity(callIntent);
    }

    public static void setMobileDataEnabled(Activity curActivity, boolean enabled) {
        final ConnectivityManager conman = (ConnectivityManager) curActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class conmanClass;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean isMobileDataEnabled(Activity curActivity) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) curActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible public  API
            // TODO do whatever error handling you want here
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }

    public static boolean isNetworkAvailable(Activity curActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) curActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isWifiEnabled(Activity curActivity) {
        WifiManager wifi = (WifiManager) curActivity.getSystemService(Context.WIFI_SERVICE);
        return (wifi.isWifiEnabled());
    }



    public static boolean isInternetEnabled(Activity curActivity) {
        if ((PhoneFunctions.isNetworkAvailable(curActivity) && PhoneFunctions.isWifiEnabled(curActivity))
                || PhoneFunctions.isMobileDataEnabled(curActivity)) {
            return true;
        } else {
            return false;
        }
    }

    public static void enableWifi(Activity curActivity, boolean state) {
        WifiManager wifi = (WifiManager) curActivity.getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(state);
    }

    public static void storeInPrivateSharedPreferences(Activity curActivity, String key, String value) {
        SharedPreferences pref = curActivity.getSharedPreferences("saveDetails", Context.MODE_PRIVATE);
        pref.edit().putString(key, value).commit();
    }

    public static String getFromPrivateSharedPreferences(Activity curActivity, String key) {
        SharedPreferences pref = curActivity.getSharedPreferences("saveDetails", Context.MODE_PRIVATE);
        return (pref.getString(key, "Not Found"));
    }
}


