package com.gsysk.parseCloudServices;

import android.app.Activity;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by lenovo on 02-03-2015.
 */
public class CloudInteractor {

    Activity curActivity = null;

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
