package com.gsysk.activity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gsysk.fma.R;
import com.gsysk.mapUtils.MapFunctions;
import com.gsysk.mapUtils.RouteFormulator;
import com.gsysk.mapUtils.RouteMarker;
import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.phoneUtils.PhoneFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutesMapActivity extends ActionBarActivity {

	GoogleMap map = null;
	RouteFormulator []routes = null;
    Marker driverMarker = null;
    private Handler handler = null;

	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
	 
	  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_routes);


        try
        {



     // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);


            // Getting Map for the SupportMapFragment
            map = fm.getMap();


            int noOfClusters = Integer.parseInt(PhoneFunctions.getFromPrivateSharedPreferences(RoutesMapActivity.this, "NoOfClusters"));

            PhoneFunctions.storeInPrivateSharedPreferences(RoutesMapActivity.this,"NumOfRoutesDrawn","0");

            String [] clusterParts = PhoneFunctions.getFromPrivateSharedPreferences(this,"DropPointList").split(" # ");
            for(int i=0;i<clusterParts.length;i++)
                System.out.println("Cluster Points : "+clusterParts[i]);
            LatLng [] sourceArray = new LatLng[noOfClusters];
            int numRoutes = 0;
            routes = new RouteFormulator[noOfClusters];
            for(int k=0;k<noOfClusters;k++)
            {
                routes[k] = new RouteFormulator(clusterParts[k]);

                Marker src = map.addMarker(new MarkerOptions().position(new LatLng(routes[k].getSource().latitude, routes[k].getSource().longitude)).title(routes[k].getSource().name).icon(BitmapDescriptorFactory.fromResource(R.drawable.star)));
                //  Marker dst = map.addMarker(new MarkerOptions().position(new LatLng(routes.getDestination().latitude,routes.getDestination().longitude)).title(routes.getDestination().name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));


                Marker [][] dropPoints = new Marker[routes[k].getNumberOfRoutes()][];

                sourceArray[k] = new LatLng(routes[k].getSource().latitude, routes[k].getSource().longitude);

                for(int i=0;i<routes[k].getNumberOfRoutes();i++) {

                    dropPoints[i] = new Marker[routes[k].getDropPoints()[i].length];
                    int count = 1;
                    for(int j=0;j<routes[k].getDropPoints()[i].length;j++,count++)
                    {
                        dropPoints[i][j] = map.addMarker(new MarkerOptions().position(new LatLng(routes[k].getDropPoints()[i][j].latitude, routes[k].getDropPoints()[i][j].longitude)).title(count + " : " +routes[k].getDropPoints()[i][j].name).icon(BitmapDescriptorFactory.fromResource(R.drawable.redbusstop)));
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

            LatLng meanSrc =meanOf(sourceArray);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(meanSrc,
                    10f));
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
 

 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.d("FMA","In service run");

            CloudInteractor Ci = new CloudInteractor(RoutesMapActivity.this);
            String driverLoc = Ci.getDriverLoc(new HashMap<String, Object>());

            if(driverLoc.startsWith("Success : "))
            {
                final String [] parts = driverLoc.substring(10).split(" : ");

                RoutesMapActivity.this.runOnUiThread(new Runnable()
                                                     {

                    @Override
                    public void run() {
                        if(driverMarker!=null)
                        {
                            driverMarker.remove();
                        }
                        driverMarker = map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(parts[1]),Double.valueOf(parts[2]))).title(parts[0]).icon(BitmapDescriptorFactory.fromResource(R.drawable.van)));

                    }
                });

            }

            handler.postDelayed(this, 10000); // 10 seconds
        }
    };
}
	


