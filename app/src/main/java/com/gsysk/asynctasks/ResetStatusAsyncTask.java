package com.gsysk.asynctasks;

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
public class ResetStatusAsyncTask extends AsyncTask<Void, Void, Void> {
    Activity curActivity = null;
    List<ParseObject> entries = null;
    List<String> droppointIDs ;
    ProgressDialog progressDialog = null;
    // Override this method to do custom remote calls
    String errorMessage = "";
    public ResetStatusAsyncTask(Activity activity, List<String> dpIds)
    {
        curActivity = activity;
        droppointIDs = dpIds;
    }
    protected Void doInBackground(Void... params) {
        // Gets the current list of gatewaylist in sorted order
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("DropPoints");
        query.orderByDescending("_created_at");

        try {
            entries = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
            errorMessage = "Error fetching details";
            return null;
        }

        for(int j=0;j<droppointIDs.size();j++)
        {
            for(int i = 0; i< entries.size();i++)
            {
                if(String.valueOf(entries.get(i).get("drpnt_id")).equals(droppointIDs.get(j)))
                {
                    entries.get(i).put("Status", "Not Delivered");
                    try {
                        entries.get(i).save();
                    } catch (ParseException e) {
                        errorMessage = "Error updating details";
                        e.printStackTrace();
                        return null;
                    }
                }

            }
        }





        return null;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = ProgressDialog.show(curActivity, "", "Updating in cloud...", true);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        // Put the list of gatewaylist into the list view
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }

        if(errorMessage.equals(""))
            ToastMessageHelper.displayLongToast(curActivity, "All delivery statuses have been reset");

        else
        {
            ToastMessageHelper.displayLongToast(curActivity,errorMessage);
            errorMessage = "";
        }

    }
}