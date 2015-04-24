package com.gsysk.parseCloudServices;

import android.app.Activity;
import android.util.Log;

import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 02-03-2015.
 */
public class CloudInteractor {

    Activity curActivity = null;
    String response = "";
  ;
    int numberOfRoutes = -1;
    String clusterNumbers = null;
    boolean canContinue_gdd = false;
    boolean canContinue_gud = false;
    boolean canContinue_login = false;
    boolean canContinue_aR = false;
    boolean canContinue_nR = false;
    boolean canContinue_gD = false;
    boolean canContinue_cladmin = false;
    boolean canContinue_cldetails = false;
    boolean canContinue_ddetails = false;
    boolean canContinue_admdetails = false;
    boolean canContinue_got_details = false;
    boolean canContinue_admdetailstodriver = false;
    boolean canContinueto_routedetails = false;
    boolean canContinue_got_routedetails = false;
    boolean canContinue_got_sourcedetails = false;

    private String resp;
    private String temp;

    boolean canContinue_gotuserdetailstodriver = false;


    private String userdetails;

    public CloudInteractor(Activity activity)
    {
        curActivity = activity;
    }
    public void initialize(String appKey, String clientKey)
    {
        Parse.initialize(curActivity, appKey, clientKey);
  //      Parse.initialize(curActivity, "XdzcMtL72Ho3GmVBbCaEY7pzdg8cGXF1EkyTbdUw", "mpsFnnEuQURv0KzH4dPy0xtV8vN8gZdRTSzCDoix");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public String getAllDetails(HashMap<String,Object> params)
    {

        try
        {

            ParseCloud.callFunctionInBackground("getAllDetails", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_gdd = true;
                }

            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        while(!canContinue_gdd)
        {

        }
        canContinue_gdd = false;
        return response;

    }

    public String verifyLogin(HashMap<String,Object> params)
    {

        try
        {

            ParseCloud.callFunctionInBackground("verifyLogin", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_login = true;
                }

            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        while(!canContinue_login)
        {

        }
        canContinue_login = false;
        return response;

    }

    public String getContactDetails(HashMap<String,Object> params)
    {

        try
        {

            ParseCloud.callFunctionInBackground("getContactDetails", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_gud = true;
                }

            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        while(!canContinue_gud)
        {

        }
        canContinue_gud = false;
        return response;

    }

    public String getDriverLoc(final HashMap<String,Object> params)
    {
        try
        {






            // routeDetails.put("Destination",params.get("Destination"));
            ParseCloud.callFunctionInBackground("getDriverLocation", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_gD = true;
                }

            });




        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        while(!canContinue_gD)
        {

        }
        canContinue_gD = false;

        return response;

    }
    public String getAllDropPointsAndRoutes(final HashMap<String,Object> params)
    {

        try
        {






           // routeDetails.put("Destination",params.get("Destination"));
            ParseCloud.callFunctionInBackground("getAllDropPoints", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_aR = true;
                }

            });




        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        while(!canContinue_aR)
        {

        }
        canContinue_aR = false;

        return response;

    }

    public String getClusterDetails(final HashMap<String,Object> params)
    {
        try
        {
            ParseCloud.callFunctionInBackground("getAdminCluster", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        clusterNumbers = value;
                        System.out.println("Cluster Number "+clusterNumbers);

                    } else {
                        System.out.println("Got error in adminCluster");
                        //response = "Error : "+e.getMessage().toString();
                    }

                    canContinue_cladmin = true;
                }

            });

            while(!canContinue_cladmin)
            {

            }
            canContinue_cladmin = false;

            HashMap<String,Object> input = new HashMap<String,Object>();
            input.put("ClusterNumber",clusterNumbers);

            ParseCloud.callFunctionInBackground("getClusterParameters", input, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                        System.out.println(response);

                    } else {
                        System.out.println("Error in clusterParameters: "+e.getMessage().toString());
                        response = "Error : "+e.getMessage().toString();
                    }

                    canContinue_cldetails = true;
                }

            });

            while(!canContinue_cldetails)
            {

            }
            canContinue_cldetails = false;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }
    public void pullAllData(String queryType,List<ParseObject> parseObjectList)
    {
        ParseQuery<ParseObject> query = null;

        List<ParseObject> parseObjects = parseObjectList;

        if(queryType.equals("Drivers"))
        {
            query = new ParseQuery<ParseObject>("Driver");
        }
        else if(queryType.equals("Users"))
        {
            query = new ParseQuery<ParseObject>("user");
        }



        try {
            parseObjectList.addAll(query.find());

           /* final List<ParseObject> finalParseObjectList = parseObjectList;
            curActivity.runOnUiThread(new Runnable() {
                public void run() {
                    ToastMessageHelper.displayLongToast(curActivity, "Objects found : ");
                    for (int i = 0; i < finalParseObjectList.size(); i++) {
                        ToastMessageHelper.displayLongToast(curActivity, finalParseObjectList.get(i).get("Name").toString());
                        System.out.println();
                    }
                }
            });
        */

        } catch (ParseException e) {
        e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public String getDriverForUser(HashMap<String,Object> params){

        try
        {

            ParseCloud.callFunctionInBackground("getdriveridforuser", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_ddetails = true;
                }

            });

            while(!canContinue_ddetails)
            {

            }
            canContinue_ddetails = false;



            String data = response.split(" ; ")[0];
            String []driver_ids = data.split(" : ");
            String driver_id = driver_ids[1];
            String admin_ids = driver_ids[2];
            Log.d("MyApp", admin_ids);

            Log.d("MyApp",driver_id);
            //HashMap<String, Object> params = new HashMap<String, Object>();
            params.remove("loginid");
            driver_id=driver_id.trim();
            params.put("driver_id",Integer.parseInt(driver_id));

            response="";

            ParseCloud.callFunctionInBackground("getdriverforuser", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        resp = "Success : "+value;
                        canContinue_admdetails = true;
                    } else {
                        System.out.println("Got error");
                        resp = "Error : "+e.getMessage().toString();
                    }

                }

            });

            while(!canContinue_admdetails)
            {

            }
            canContinue_admdetails = false;


            params.put("adminIdArray",admin_ids);

            //response="";
            Log.d("MyApp","Here");
            ParseCloud.callFunctionInBackground("getadminstouser", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        resp = resp.concat("#Success : "+value);
                        Log.d("MyApp",resp);

                        canContinue_got_details = true;
                    } else {
                        System.out.println("Got error");
                        resp = "Error : "+e.getMessage().toString();
                    }

                }

            });

            while(!canContinue_got_details)
            {

            }
            canContinue_got_details = false;



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.d("MyApp",resp);

        return resp;

    }


    public String getDetailsToDriver(HashMap<String,Object> params){

        try
        {

            ParseCloud.callFunctionInBackground("getdetailstoDriver", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        response = "Success : "+value;
                    } else {
                        System.out.println("Got error");
                        response = "Error : "+e.getMessage().toString();
                    }
                    canContinue_ddetails = true;
                }

            });

            while(!canContinue_ddetails)
            {

            }
            canContinue_ddetails = false;



            String data = response.split(" ; ")[0];
            String userids = data.split(" : ")[1];
            String admin_ids = data.split(" : ")[2];
            String drop_pts = data.split(" : ")[3];
            String routeid = data.split(" : ")[4];
            String clusterid = data.split(" : ")[5];

            Log.d("MyApp",data);
            Log.d("MyApp",userids);
            Log.d("MyApp",admin_ids);
            Log.d("MyApp",drop_pts);
            Log.d("MyApp",routeid);
            Log.d("MyApp",clusterid);
            //HashMap<String, Object> params = new HashMap<String, Object>();
            //params.remove("loginid");
            //driver_id=driver_id.trim();
            params.put("uidsArray",userids);
            params.put("adminIdArray",admin_ids);
            params.put("dpidsArray",drop_pts);
            params.put("cluster_id",Integer.parseInt(clusterid));

            response="";

            ParseCloud.callFunctionInBackground("getuserstodriver", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        resp = "Success : "+value;
                        canContinue_admdetailstodriver = true;
                    } else {
                        System.out.println("Got error");
                        resp = "Error : "+e.getMessage().toString();
                    }
                }

            });

            while(!canContinue_admdetailstodriver)
            {

            }
            canContinue_admdetailstodriver = false;

            //response="";
            //Log.d("MyApp","Here");
            ParseCloud.callFunctionInBackground("getadminstodriver", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        resp = resp.concat("#Success : "+value);
                        Log.d("MyApp--getdriversroute",resp);

                        canContinueto_routedetails = true;
                    } else {
                        System.out.println("Got error");
                        resp = "Error : "+e.getMessage().toString();
                    }

                }

            });

            while(!canContinueto_routedetails)
            {

            }
            canContinueto_routedetails = false;

            ParseCloud.callFunctionInBackground("getSourceNametoDriver", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {

                        //resp = resp.concat("#Success : "+value);
                        temp = "Success : "+value;

                        Log.d("MyApp",temp);

                        canContinue_got_sourcedetails = true;
                    } else {
                        System.out.println("Got error");
                        resp = "Error : "+e.getMessage().toString();
                    }

                }

            });

            while(!canContinue_got_sourcedetails)
            {

            }
            canContinue_got_sourcedetails = false;

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

            ParseCloud.callFunctionInBackground("getuserstodriverwithdrpids", params, new FunctionCallback<String>() {
                public void done(String value, ParseException e) {
                    if (e == null) {
                        Log.d("MyApp-UserValues",value);
                        userdetails = "Success : "+value;
                        Log.d("MyApp-UserValues",userdetails);
                        canContinue_gotuserdetailstodriver = true;
                    } else {
                        System.out.println("Got error");
                        userdetails = "Error : "+e.getMessage().toString();
                    }
                }

            });



            while(!canContinue_gotuserdetailstodriver)
            {

            }
            canContinue_gotuserdetailstodriver = false;

            if(userdetails.startsWith("Success : "))
            {
                Log.d("MyApp","Store MyApp-UserValues in shared prifferenece "+userdetails);
                PhoneFunctions.storeInPrivateSharedPreferences(curActivity, "usersToDriverwithDPName", userdetails.substring(10));

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.d("MyApp",resp);

        return resp;

    }
}


