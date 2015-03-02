package com.gsysk.guiDisplays;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by lenovo on 02-03-2015.
 */
public class ToastMessageHelper {

    public static void displayShortToast(Activity activity, String message)
    {
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
    }

    public static void displayLongToast(Activity activity, String message)
    {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
    }
}
