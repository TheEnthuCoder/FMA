package com.gsysk.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.gsysk.activity.AdminButtonActivity;
import com.gsysk.activity.MapActivityUser;
import com.gsysk.activity.MapsDriverActivity;
import com.gsysk.constants.ConstantValues;
import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.guiDisplays.AlertDialogHelper;

public class CloudParserAsyncTask extends AsyncTask<Void, Void, Void> {

    // Override this method to do custom remote calls
    int j = 0;
    String queryType = "";
    Activity curActivity;
    ProgressDialog progressDialog;
    CloudInteractor pullAllService = null;
    String response ="";

    String username = "";
    String password = "";

    String status = "";
    String roleType="";

    Boolean skipLogin = false;
    public CloudParserAsyncTask(Activity activity,String username,String password)
    {
      //  queryType = type;
        curActivity = activity;
        this.username = username;
        this.password = password;


      //  this.parseObjectList = parseObjectList;
        this.pullAllService = pullAllService;
    }
    public CloudParserAsyncTask(Activity activity,String roleType)
    {
        //  queryType = type;
        curActivity = activity;
        skipLogin = true;
        this.roleType = roleType.split(" : ")[0];
        this.username = roleType.split(" : ")[1];
    }
    protected Void doInBackground(Void... params)
    {
        //Initiate processing and next activity launch

        // Gets the current list of parseObjectsList.gatewaylist in sorted order


            try
            {
                if(skipLogin)
                {
                    status = "Success";

                }
                else
                {
                    status = verifyLogin();
                    if(status.startsWith("Success"))
                    {
                        String []role = status.split(":");
                        status = role[0];
                        roleType = role[1];
                    }
                    else
                    {
                        displayToastInUi(status);
                        return null;
                    }
                }

                if(roleType.equals(ConstantValues.ROLE_ADMIN))
                {
                    doAdminTasks();
                }
                else if(roleType.equals(ConstantValues.ROLE_USER))
                {
                    doContactTasks();
                }
                else if(roleType.equals(ConstantValues.ROLE_DRIVER))
                {
                    doDriverTasks();
                }


            }
            catch (Exception e)
            {
                displayToastInUi(ConstantValues.PLEASE_TRY_AGAIN);
                e.printStackTrace();
            }


        return null;
    }

    @Override
    protected void onPreExecute() {

        pullAllService = new CloudInteractor(curActivity);

        pullAllService.initialize(ConstantValues.APP_KEY,ConstantValues.CLIENT_KEY);
        progressDialog = ProgressDialog.show(curActivity, "",ConstantValues.LOGIN_PROGRESS, true);


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

        if(status.equals("Success"))
        {
            if(roleType.equals(ConstantValues.ROLE_ADMIN)&&!skipLogin)
            {
                Intent intent = new Intent(curActivity.getApplicationContext(), AdminButtonActivity.class);

                Bundle b = new Bundle();
                b.putString("UserName",username);
                intent.putExtra("DataBundle",b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(intent);

                skipLogin = false;
            }
            else if(roleType.equals(ConstantValues.ROLE_USER))
            {
                Intent intent = new Intent(curActivity.getApplicationContext(), MapActivityUser.class);
                Bundle b = new Bundle();
                b.putString("UserName",username);
                intent.putExtra("DataBundle",b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(intent);

                skipLogin = false;
            }
            else if(roleType.equals(ConstantValues.ROLE_DRIVER))
            {
                Intent intent = new Intent(curActivity.getApplicationContext(), MapsDriverActivity.class);
                Bundle b = new Bundle();
                b.putString("UserName",username);
                intent.putExtra("DataBundle",b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(intent);

                skipLogin = false;
            }
        }


    }

    private void displayToastInUi(final String message)
    {
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               ToastMessageHelper.displayLongToast(curActivity,message);
            }
        });
    }
    private void updateProgressDialogWith(final String message)
    {
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
            }
        });
    }

    private void doAdminTasks()
    {
        HashMap<String, Object> input = new HashMap<String, Object>();

        updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);
        response = pullAllService.getDriverDetails(input);
        if(response.startsWith("Success : "))
        {
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfDrivers",response.substring(10));

        }

        response="";
       // updateProgressDialogWith(ConstantValues.USER_PROGRESS);
        response = pullAllService.getContactDetails(input);
        if(response.startsWith("Success : "))
        {
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfContacts",response.substring(10));
        }
        response="";

        curActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run() {
                System.out.println(PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"UserName"));
            }
        });
        input.put("Name",username);

        response = pullAllService.getClusterDetails(input);
        if(response.startsWith("Success : "))
        {
            response = response.substring(10);
        }


        input.clear();

        String responseCopy = new String(response);
        response="";
        final String [] clusters = responseCopy.split(" # ");
        PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"NoOfClusters",String.valueOf(clusters.length));
        for(int x=0;x<clusters.length;x++)
        {
            //Getting all drop points of the given cluster, according to various routes belonging to that cluster
            input.put("Source",clusters[x].split(" : ")[0]);
            final String source = clusters[x].split(" : ")[0];
            final String routes = clusters[x].split(" : ")[1];
            curActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Source : "+source);
                    System.out.println("Routes : "+routes);
                }
            });
            input.put("RouteNumArray", routes);
            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //  input.put("Destination","Hebbal");
            if(x==0)
            {
                response = response.concat(pullAllService.getAllDropPointsAndRoutes(input)+" # ");
            }
            else
            {
                response = response.concat(pullAllService.getAllDropPointsAndRoutes(input).substring(10)+" # ");
            }


            input.clear();
        }


       // displayToastInUi(response);
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Points : "+response);
            }
        });

        if(response.startsWith("Success : "))
        {
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"DropPointList",response.substring(10));
        }
        response="";
    }

    private void doContactTasks()
    {

    }

    private void doDriverTasks()
    {

    }

    private String verifyLogin()
    {
        HashMap<String,Object> loginDetails = new HashMap<String, Object>();
        loginDetails.put("Name",username);
        loginDetails.put("Password",password);
        response = pullAllService.verifyLogin(loginDetails);

        return(response);
    }

}