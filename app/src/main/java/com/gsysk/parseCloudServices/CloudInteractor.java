package com.gsysk.parseCloudServices;

import android.app.Activity;

import com.gsysk.guiDisplays.ToastMessageHelper;
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
    boolean canContinue_cladmin = false;
    boolean canContinue_cldetails = false;
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

    public String getDriverDetails(HashMap<String,Object> params)
    {

        try
        {

            ParseCloud.callFunctionInBackground("getDriverDetails", params, new FunctionCallback<String>() {
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
}
