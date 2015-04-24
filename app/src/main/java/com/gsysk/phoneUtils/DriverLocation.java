package com.gsysk.phoneUtils;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

/**
 * Created by SatishPK on 3/25/2015.
 */
public class DriverLocation  extends Service {


    public static final String BROADCAST_ACTION  = "MY_ACTION";
    Location [] location; // location
    private double [] latitude; // latitude
    private double [] longitude; // longitude
    Intent intent;
    private final Handler handler = new Handler();
    String response = "";
    String routeNumbers="";
    int count = 0;
    private boolean flag = false;

    public DriverLocation() {



    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Location [] getLocation(){

        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("vehiclelocation");
        List<ParseObject> vehicleloc;
        ParseObject loc = new ParseObject("vehiclelocation");

        query.whereEqualTo("vehicleid",1);
        try {
            vehicleloc = query.find();
            loc = vehicleloc.get(0);
            latitude= loc.getDouble("latitude");
            longitude = loc.getDouble("longitude");



        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

        HashMap<String,Object> params = new HashMap<String,Object>();
        System.out.println("Route Numbers : "+routeNumbers.substring(0,routeNumbers.length()-3));
        params.put("RouteNumbers",routeNumbers.substring(0,routeNumbers.length()-3));



        ParseCloud.callFunctionInBackground("getDriverLocationForRoutes",params,new FunctionCallback<String>() {

            public void done(String value, ParseException e) {
                if (e == null) {
                    response = value;
                    Log.d("MyApp",response);
                    String [] parts = response.split(" ; ");

                    latitude = new double[parts.length];
                    longitude = new double[parts.length];
                    Log.i("Sequence","Inside bg function");
                    for(int i=0;i<parts.length;i++)
                    {
                        String[] loc = parts[i].split(" : ");
                        latitude[i] = Double.parseDouble(loc[0]);
                        longitude[i] = Double.parseDouble(loc[1]);
                    }

                    count= parts.length;

                    //Log.d("MyApp",loc[0]);
                    //Log.d("MyApp",loc[1]);
                    //Log.d("MyApp",loc[2]);
                } else {
                    Log.d("MyApp","Unable Fetch Driverloc details");
                    response = "Error : "+e.getMessage().toString();

                }

            }
        });


        Log.i("Sequence","Inside main thread of driver loc");
        response = "";
        return location;


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApp", "In service oncreate");


        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        routeNumbers = intent.getStringExtra("RouteNumbers");
                getLocation();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 10000); // 10 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.d("MyApp","In service run");

            Bundle loc = new Bundle();
            loc.putDoubleArray("LATITUDE",latitude);
            loc.putDoubleArray("LONGITUDE",longitude);
            loc.putInt("Count",count);
            intent.setAction(BROADCAST_ACTION);

            intent.putExtra("LOC",loc);

            sendBroadcast(intent);

            //Call getLocation again to fetch next location update, thus, repeatedly calling getLocation
            getLocation();
            latitude = null;
            longitude = null;

            handler.postDelayed(this, 10000); // 10 seconds
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(sendUpdatesToUI);



    }
}
