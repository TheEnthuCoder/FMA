package com.gsysk.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
    Boolean showProgress = true;
    Boolean skipLogin = false;

    String loginid="";
    public CloudParserAsyncTask(Activity activity,String username,String password)
    {
      //  queryType = type;
        curActivity = activity;
        this.username = username;
        this.password = password;


      //  this.parseObjectList = parseObjectList;
        this.pullAllService = pullAllService;
    }
    public CloudParserAsyncTask(Activity activity,String roleType,boolean showProgress)
    {
        //  queryType = type;
        curActivity = activity;
        skipLogin = true;
        this.roleType = roleType.split(" : ")[0];
        this.username = roleType.split(" : ")[1];
        this.showProgress = showProgress;
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
                        loginid = role[2];
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

       //  pullAllService.initialize(ConstantValues.APP_KEY,ConstantValues.CLIENT_KEY);
        if(showProgress)
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

        if(showProgress)
          progressDialog.dismiss();

        if(!showProgress)
            ToastMessageHelper.displayLongToast(curActivity,"Data Refreshed");
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
            else if(roleType.equals(ConstantValues.ROLE_USER)&&!skipLogin)
            {
                Intent intent = new Intent(curActivity.getApplicationContext(), MapActivityUser.class);
                Bundle b = new Bundle();
                b.putString("UserName",username);
                intent.putExtra("DataBundle",b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(intent);

                skipLogin = false;
            }
            else if(roleType.equals(ConstantValues.ROLE_DRIVER)&&!skipLogin)
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

        if(showProgress)
        updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);

        input.put("Name",username);


        response = pullAllService.getAllDetails(input);
        String []parts  = null;
        String clusterDet = "";
        if(response.startsWith("Success : "))
        {
            String resp = response.substring(10);
            System.out.println("Concatenated response : "+resp);
            parts = resp.split(" \\| ");
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfDrivers",parts[0]);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfContacts", parts[1]);
            //clusterDet = parts[2];
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"NoOfClusters",parts[2]);

            String []pointsResponse= parts[3].split("\\.,");
           response = "";
            for(int x =0;x<pointsResponse.length;x++)
            {

                response = response.concat(pointsResponse[x]+ " # ");
            }

            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"DropPointList",response);

        }

      //  response="";
       // updateProgressDialogWith(ConstantValues.USER_PROGRESS);
      /*  response = pullAllService.getContactDetails(input);
        if(response.startsWith("Success : "))
        {
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfContacts",response.substring(10));
        }
        response="";
*/
        curActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run() {
                System.out.println(PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"UserName"));
            }
        });


      /*  response = pullAllService.getClusterDetails(input);
        if(response.startsWith("Success : "))
        {
            response = response.substring(10);
        }

*/
       // input.clear();

        /*String responseCopy = new String(clusterDet);
        response="";
        final String [] clusters = responseCopy.split(" # ");
        PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"NoOfClusters",String.valueOf(clusters.length));
        for(int x=0;x<clusters.length;x++)
        {
            //Getting all drop points of the given cluster, according to various routes belonging to that cluster
            System.out.println("Source : "+ clusters[x].split(" : ")[0]);
            System.out.println("Routes : "+ clusters[x].split(" : ")[1]);
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

*/
      // displayToastInUi(response);
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Points : "+response);
            }
        });

        /*if(response.startsWith("Success : "))
        {
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"DropPointList",response.substring(10));
        }
        */
        response="";
    }

    private void doContactTasks()
    {
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("loginid",loginid);
        updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);
        response = pullAllService.getDriverForUser(input);
        String resp1 = response.split("#")[0];
        String resp2 = response.split("#")[1];
        if(resp1.startsWith("Success : "))
        {
            Log.d("MyApp", "Store in shared prifferenece " + resp1);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"driverForUser",resp1.substring(10));

        }

        if(resp2.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp2);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"adminForUser",resp2.substring(10));

        }

        response="";




    }

    private void doDriverTasks()
    {
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("loginid",loginid);
        updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);
        response = pullAllService.getDetailsToDriver(input);

        String resp1 = response.split("#")[0];
        String resp2 = response.split("#")[1];
        String resp3 = response.split(("#"))[2];
        String resp4 = response.split("#")[3];

        if(resp1.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp1);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"UsersToDriver",resp1.substring(10));

        }

        if(resp2.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp2);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"adminForDriver",resp2.substring(10));

        }
        if(resp3.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp3);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"routeForDriver",resp3.substring(10));

        }
        if(resp4.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp4);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"SourceToDriver",resp4.substring(10));

        }

        response="";


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