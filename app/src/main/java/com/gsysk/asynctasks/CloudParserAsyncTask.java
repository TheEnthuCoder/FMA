package com.gsysk.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.gsysk.constants.ConstantValues;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;


import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.guiDisplays.AlertDialogHelper;

public class CloudParserAsyncTask extends AsyncTask<Void, Void, Void> {

    // Override this method to do custom remote calls
    int j = 0;
    String queryType = "";
    Activity curActivity;
    List<ParseObject> parseObjectList;
    ProgressDialog progressDialog;
    CloudInteractor pullAllService = null;
    public CloudParserAsyncTask(String type,Activity activity,List<ParseObject> parseObjectList, CloudInteractor pullAllService)
    {
        queryType = type;
        curActivity = activity;
        this.parseObjectList = parseObjectList;
        this.pullAllService = pullAllService;
    }
    protected Void doInBackground(Void... params)
    {
        //Initiate processing and next activity launch

        // Gets the current list of parseObjectsList.gatewaylist in sorted order
        parseObjectList = new ArrayList<ParseObject>();
        pullAllService.pullAllData(queryType,parseObjectList);


        return null;
    }

    @Override
    protected void onPreExecute() {

        if(queryType.equals("Drivers"))
        {
            progressDialog = ProgressDialog.show(curActivity, "",

                    ConstantValues.DRIVER_PROGRESS, true);
        }
        else if(queryType.equals("Users"))
        {
            progressDialog = ProgressDialog.show(curActivity, "",
                    ConstantValues.USER_PROGRESS, true);
        }

        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        // Put the list of parseObjectsList.gatewaylist into the list view


        progressDialog.dismiss();
        String titleMessage = "";
        if(queryType.equals("Drivers"))
        {
            titleMessage = "List of Drivers : ";



        }
        else if(queryType.equals("Users"))
        {
            titleMessage = "List of Contacts : ";
        }

        AlertDialogHelper createdDialog = new AlertDialogHelper(curActivity,titleMessage);
        createdDialog.createListAlertDialog(parseObjectList);

    }



}