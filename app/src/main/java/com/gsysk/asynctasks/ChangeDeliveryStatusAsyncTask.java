package com.gsysk.asynctasks;

/**
 * Created by lenovo on 28-04-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.gsysk.guiDisplays.ToastMessageHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.gsysk.guiDisplays.ToastMessageHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by lenovo on 27-04-2015.
 */
public class ChangeDeliveryStatusAsyncTask extends AsyncTask<Void, Void, Void> {
    Activity curActivity = null;
    ProgressDialog progressDialog = null;

    int drp_id;
    String updateString="";
    // Override this method to do custom remote calls
    String errorMessage = "";
    public ChangeDeliveryStatusAsyncTask(Activity activity,int drp_id,String updateString)
    {
        curActivity = activity;
        this.drp_id = drp_id;
        this.updateString = updateString;

    }
    protected Void doInBackground(Void... params) {
        // Gets the current list of gatewaylist in sorted order


        ParseQuery<ParseObject> query = ParseQuery.getQuery("DropPoints");
        List<ParseObject> drpts;
        ParseObject drpnt;// = new ParseObject("vehiclelocation");

        query.whereEqualTo("drpnt_id",drp_id);
        try {
            drpts = query.find();
            drpnt = drpts.get(0);
            drpnt.put("Status", updateString);
            drpnt.saveInBackground();


        } catch (ParseException e) {
            e.printStackTrace();
            errorMessage = "Error updating details";
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

       // progressDialog = ProgressDialog.show(curActivity, "", "Updating in cloud...", true);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        // Put the list of gatewaylist into the list view
      //  if(progressDialog!=null)
       // {
         //   progressDialog.dismiss();
        //}

        if(errorMessage.equals(""))
            ToastMessageHelper.displayLongToast(curActivity, "Update complete");

        else
        {
            ToastMessageHelper.displayLongToast(curActivity,errorMessage);
            errorMessage = "";
        }

    }
}