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
import com.gsysk.activity.RoutesMapActivity;
import com.gsysk.activity.VehicleTrackerMapActivity;
import com.gsysk.constants.ConstantValues;
import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.guiDisplays.AlertDialogHelper;
import com.parse.ParseQuery;

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

    String temp="";
    boolean canContinue_got_sourcedetails = false;
    String resp = "";
    boolean canContinue_got_routedetails = false;

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

        loginid = PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"LoginID");

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
                        PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"RoleTypeOfLoggedIn",roleType);
                        loginid = role[2];
                        PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"LoginID",loginid);
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
                b.putString("UserName", username);
                PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"UserName",username);
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
                PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"UserName",username);
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
                PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"UserName",username);
                intent.putExtra("DataBundle",b);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(intent);

                skipLogin = false;
            }

            if(curActivity.getClass().isAssignableFrom(RoutesMapActivity.class))
            {
                ((RoutesMapActivity) curActivity).resetMap();
            }
            else if(curActivity.getClass().isAssignableFrom(VehicleTrackerMapActivity.class))
            {
                ((VehicleTrackerMapActivity) curActivity).resetMap();
            }
            else if(curActivity.getClass().isAssignableFrom(MapsDriverActivity.class))
            {
                ((MapsDriverActivity) curActivity).resetMap();
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
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfVehicles", parts[1]);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"ListOfContacts", parts[2]);
            //clusterDet = parts[2];
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"NoOfClusters",parts[3]);

            String []pointsResponse= parts[4].split("\\.,");
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
        if(showProgress)
            updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);

        getVehicleIdToUser();
        //int drp_Pnt_Id = getDropPointId();

        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("loginid",loginid);

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
        if(showProgress)
            updateProgressDialogWith(ConstantValues.CONTENT_PROGRESS);

        int vehicleId = getVehicleId();



        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("loginid",loginid);

        response = pullAllService.getDetailsToDriver(input);

        String resp1 = response.split("#")[0];
        String resp2 = response.split("#")[1];
        String resp3 = response.split(("#"))[2];
        String resp4 = response.split("#")[3];

        Log.d("MyApp-Source",resp4);

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

    private int getVehicleId() {
        int v_id = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
        List<ParseObject> driverObj;
        ParseObject d = new ParseObject("Driver");

        query.whereEqualTo("loginid",loginid);
        try {
            driverObj = query.find();
            d = driverObj.get(0);
            v_id= d.getInt("v_id");

            Log.d("MyApp","V _id Store in shared prifferenece "+ String.valueOf(v_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"vehicle_id",String.valueOf(v_id));




        } catch (ParseException e) {
            e.printStackTrace();
        }



        return v_id;

    }

    private void getVehicleIdToUser() {

        int v_id=0;
        int drp_id=0;
        int d_id =0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("user");
        List<ParseObject> userObj;
        ParseObject u = new ParseObject("user");

        query.whereEqualTo("loginid",loginid);
        try {
            userObj = query.find();
            u = userObj.get(0);
            v_id= u.getInt("v_id");
            drp_id= u.getInt("drppnt_id");
            d_id = u.getInt("d_id");

            Log.d("MyApp","drp_id Store in shared prifferenece "+ String.valueOf(drp_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"drp_id_user",String.valueOf(drp_id));

            Log.d("MyApp","v_id Store in shared prifferenece "+ String.valueOf(v_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"vehicle_id_user",String.valueOf(v_id));

            Log.d("MyApp","d_id Store in shared prifferenece "+ String.valueOf(d_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"d_id_user",String.valueOf(d_id));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("MyApp-d_id",String.valueOf(d_id));
        getDriverdetails(d_id);
        //return v_id;
    }

    public void getDriverdetails(int d_id){
        int  cluster_id = 0;
        //List d_ids;
        String dpidsArray = "";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
        List<ParseObject> driverObj;
        ParseObject d = new ParseObject("Driver");

        query.whereEqualTo("driver_id",d_id);
        try {
            driverObj = query.find();
            d = driverObj.get(0);
            cluster_id= d.getInt("cluster_id");
            //d_ids = d.getList("r_droppoints");
            dpidsArray = d.get("r_droppoints").toString();


            Log.d("MyApp-dids",dpidsArray);

            Log.d("MyApp","cluster_id Store in shared prifferenece "+ String.valueOf(cluster_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"d_lgn_id_user",String.valueOf(cluster_id));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        dpidsArray = dpidsArray.replace("[","");
        dpidsArray = dpidsArray.replace("]","");


        Log.d("MyApp-dids",dpidsArray);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("cluster_id",cluster_id);
        params.put("dpidsArray",dpidsArray);


        ParseCloud.callFunctionInBackground("getSourceNametoDriver", params, new FunctionCallback<String>() {
            public void done(String value, ParseException e) {
                if (e == null) {

                    //resp = resp.concat("#Success : "+value);
                    temp = "Success : " + value;

                    Log.d("MyApp", temp);

                    canContinue_got_sourcedetails = true;
                } else {
                    System.out.println("Got error");
                    resp = "Error : " + e.getMessage().toString();
                }

            }

        });

        while(!canContinue_got_sourcedetails)
        {

        }
        canContinue_got_sourcedetails = false;

        Log.d("MyApp-temp-value",temp);
        String src = temp.split(" ; ")[0];
        String srcName = src.split(" : ")[1];

        Log.d("MyApp",src);
        Log.d("MyApp","SRC NAME");
        Log.d("MyApp",srcName);

        params.put("srcName",srcName);

        ParseCloud.callFunctionInBackground("getdriversRoute", params, new FunctionCallback<String>() {
            public void done(String value, ParseException e) {
                if (e == null) {

                    resp = resp.concat("#Success : "+value);
                    Log.d("MyApp--getdriversroute",resp);

                    canContinue_got_routedetails = true;
                } else {
                    System.out.println("Got error");
                    resp = "Error : "+e.getMessage().toString();
                }

            }

        });

        while(!canContinue_got_routedetails)
        {

        }
        canContinue_got_routedetails = false;

        Log.d("MyApp-User Route",resp);
        String resp2 = resp.split("#")[1];
        String resp3 = resp.split(("#"))[2];

        if(resp2.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp2);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"routeForDriverU",resp2.substring(10));

        }
        if(resp3.startsWith("Success : "))
        {
            Log.d("MyApp","Store in shared prifferenece "+resp3);
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"SourceToDriverU",resp3.substring(10));

        }


    }

    /*private int getDropPointId() {

        int drp_id=0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("user");
        List<ParseObject> userObj;
        ParseObject u = new ParseObject("user");

        query.whereEqualTo("loginid",loginid);
        try {
            userObj = query.find();
            u = userObj.get(0);
            drp_id= u.getInt("drppnt_id");

            Log.d("MyApp","V _id Store in shared prifferenece "+ String.valueOf(drp_id));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"drp_id_user",String.valueOf(drp_id));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return drp_id;
    }*/
}