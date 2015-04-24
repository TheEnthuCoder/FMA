package com.gsysk.activity;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gsysk.asynctasks.CloudParserAsyncTask;
import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.mapUtils.MapFunctions;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.gsysk.mapUtils.*;
import com.gsysk.phoneUtils.*;

import java.util.ArrayList;
import java.util.List;

public class VehicleTrackerMapActivity extends ActionBarActivity {

	GoogleMap map = null;
	RouteFormulator [] routes = null;
    Intent intent = null;
    Marker []vehicle = null;
    MarkerOptions options = null;
    IntentFilter intentFilter = null;
    private String username = "";
    String routeNumArray = "";

	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
	 
	  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_tracking);

        intentFilter = new IntentFilter();
        intentFilter.addAction("START_REFRESH_OF_MAP");
        intentFilter.addAction("START_REFRESH");

        username = getIntent().getStringExtra("Username");

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);


        // Getting Map for the SupportMapFragment
        map = fm.getMap();
        setUpMap(true);
    }

    private LatLng meanOf(LatLng [] sourceArray)
    {
        int num = sourceArray.length;
        float meanLat = 0.0f;
        float meanLong = 0.0f;
        for(int i=0;i<num;i++)
        {
            meanLat +=sourceArray[i].latitude;
            meanLong+=sourceArray[i].longitude;
        }
        meanLat /= num;
        meanLong /= num;


        return(new LatLng(meanLat,meanLong));
    }

 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        startService(intent);


        registerReceiver(broadcastreceiver, new IntentFilter(DriverLocation.BROADCAST_ACTION));

        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(intent);
        unregisterReceiver(broadcastreceiver);

        Log.i("Debug","In onpause of vehicle location");
        try
        {
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    private BroadcastReceiver broadcastreceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           try
           {
               Bundle loc = intent.getBundleExtra("LOC");
               double [] latitude = loc.getDoubleArray("LATITUDE");
               double [] longitude = loc.getDoubleArray("LONGITUDE");
               Log.d("Myapp", String.valueOf(latitude));
               Log.i("Updates","Latitude array length : "+latitude.length);
         /*   Toast.makeText(VehicleTrackerMapActivity.this,
                    "Triggered by Service!\n"
                            + "Data passed: Latitude : " + String.valueOf(latitude) + " Longitude : " + String.valueOf(longitude),
                    Toast.LENGTH_LONG).show();*/

               handleNewLocation(latitude,longitude,loc.getInt("Count"));
           }
           catch(Exception e)
           {
               e.printStackTrace();
           }

        }
    };

    private void handleNewLocation(double [] currentLatitude,double [] currentLongitude, int count) {


        //storeLocationInCloud(currentLatitude,currentLongitude);
        System.out.println("No. of vehicles on map : "+count);
        try
        {
            for(int i=0;i<count;i++)
            {
                LatLng latLng = new LatLng(currentLatitude[i], currentLongitude[i]);


                options = new MarkerOptions().position(latLng).title("Driver : "+getRouteDriver(routeNumArray.split(" , ")[i])).icon(BitmapDescriptorFactory.fromResource(ConstantValues.VEHICLE_MARKER_COLOURS[i%5]));
                if(vehicle[i]!=null){
                    vehicle[i].remove();
                }
                vehicle[i] = map.addMarker(options);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }


    }

    public void setUpMap(boolean shouldZoom)
    {
        try
        {






            int noOfClusters = Integer.parseInt(PhoneFunctions.getFromPrivateSharedPreferences(VehicleTrackerMapActivity.this, "NoOfClusters"));

            PhoneFunctions.storeInPrivateSharedPreferences(VehicleTrackerMapActivity.this,"NumOfRoutesDrawn","0");

            String [] clusterParts = PhoneFunctions.getFromPrivateSharedPreferences(this,"DropPointList").split(" # ");
            for(int i=0;i<clusterParts.length;i++)
                System.out.println("Cluster Points : "+clusterParts[i]);
            LatLng [] sourceArray = new LatLng[noOfClusters];
            int numRoutes = 0;
            routes = new RouteFormulator[noOfClusters];


            for(int k=0;k<noOfClusters;k++)
            {
                routes[k] = new RouteFormulator(clusterParts[k]);


                Marker src = map.addMarker(new MarkerOptions().position(new LatLng(routes[k].getSource().latitude, routes[k].getSource().longitude)).title(routes[k].getSource().name).snippet("Cluster "+(k+1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.star)));
                //  Marker dst = map.addMarker(new MarkerOptions().position(new LatLng(routes.getDestination().latitude,routes.getDestination().longitude)).title(routes.getDestination().name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));


                Marker [][] dropPoints = new Marker[routes[k].getNumberOfRoutes()][];

                sourceArray[k] = new LatLng(routes[k].getSource().latitude, routes[k].getSource().longitude);

                for(int i=0;i<routes[k].getNumberOfRoutes();i++) {



                    dropPoints[i] = new Marker[routes[k].getDropPoints()[i].length];
                    int count = 1;
                    for(int j=0;j<routes[k].getDropPoints()[i].length;j++,count++)
                    {
                        if(j==0)
                        {
                            routeNumArray = routeNumArray.concat(routes[k].getDropPoints()[i][j].routeNum+" , ");
                            numRoutes++;
                        }

                        if(routes[k].getDropPoints()[i][j].status.equals("Not Delivered"))
                            dropPoints[i][j] = map.addMarker(new MarkerOptions().position(new LatLng(routes[k].getDropPoints()[i][j].latitude, routes[k].getDropPoints()[i][j].longitude)).title(count + " : " +routes[k].getDropPoints()[i][j].name).snippet("Route "+routes[k].getDropPoints()[i][j].routeNum).icon(BitmapDescriptorFactory.fromResource(R.drawable.redbusstop)));
                        else if(routes[k].getDropPoints()[i][j].status.equals("Delivered"))
                        {
                            dropPoints[i][j] = map.addMarker(new MarkerOptions().position(new LatLng(routes[k].getDropPoints()[i][j].latitude, routes[k].getDropPoints()[i][j].longitude)).title(count + " : " +routes[k].getDropPoints()[i][j].name).snippet("Route "+routes[k].getDropPoints()[i][j].routeNum).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenbusstop)));
                        }
                    }

                }



             /*   while(PhoneFunctions.getFromPrivateSharedPreferences(RoutesMapActivity.this,"NumOfRoutesDrawn")!=""+(numRoutes+routes.getNumberOfRoutes()))
                {

                }*/


            }


            MapFunctions.plotAllRoutes(this,map,routes);
        /*    for(int i=0;i<routes.length;i++)
            {

                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(getMapsApiDirectionsUrl(routes[i],i));
            }
*/
           /* final CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(meanOf(sourceArray),10f);



          // Initializing
            // markerPoints = new ArrayList<LatLng>();
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
                    map.animateCamera(cu);

                }
            });
*/

            vehicle = new Marker[numRoutes];
            intent = new Intent(VehicleTrackerMapActivity.this, DriverLocation.class);
            intent.putExtra("RouteNumbers",routeNumArray);
           LatLng meanSrc =meanOf(sourceArray);

            if(shouldZoom)
            {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(meanSrc,
                        10f));
            }
/*
            handler = new Handler();
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 10000); // 10 second
            */

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //   }
        //  });
        //  }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("START_REFRESH_OF_MAP"))
            {
                new CloudParserAsyncTask(VehicleTrackerMapActivity.this,"admin"+" : "+username,false).execute();

            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();


        try
        {
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }


    public void resetMap()
    {
        if(map!=null)
        {
            map.clear();
            setUpMap(false);
        }
    }

    public String getRouteDriver(String routeNum)
    {
        String driver = "";

        String allDrivers = PhoneFunctions.getFromPrivateSharedPreferences(VehicleTrackerMapActivity.this,"ListOfDrivers");
        String []indDriver = allDrivers.split(" ; ");

        for(int i=0;i<indDriver.length;i++)
        {
            String [] parts = indDriver[i].split(" : ");
            if(parts[2].equals(routeNum))
            {
                driver = parts[0];
                break;
            }

        }


        return driver;
    }
	
}

