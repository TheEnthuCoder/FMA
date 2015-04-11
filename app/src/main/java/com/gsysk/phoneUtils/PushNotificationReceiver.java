package com.gsysk.phoneUtils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.gsysk.activity.AdminButtonActivity;
import com.gsysk.asynctasks.CloudParserAsyncTask;
import com.gsysk.guiDisplays.ToastMessageHelper;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 09-04-2015.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "PushNotificationReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent: " + intent.toString());
        String action = intent.getAction();
        Log.d("TEST", action.toString());
        if (action.equals(ParsePushBroadcastReceiver.ACTION_PUSH_RECEIVE)) {
            JSONObject extras;
            try {
                extras = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));

                // I get this on my log like this:
                // Received push notification. Alert: A test push from Parse!

                Log.i(TAG, "Received push notification. Alert: " + extras.getString("alert"));

                if(context!=null)
                {
                    Toast.makeText(context, "Change in cloud tracked..refreshing",Toast.LENGTH_LONG).show();

                    SharedPreferences pref = context.getSharedPreferences("saveDetails", Context.MODE_PRIVATE);

                    // send result back to Caller Activity
                    Intent replyIntent = new Intent("START_REFRESH");
                    context.sendBroadcast(replyIntent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}