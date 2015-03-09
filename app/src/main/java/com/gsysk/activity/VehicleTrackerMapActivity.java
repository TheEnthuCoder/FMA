package com.gsysk.activity;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gsysk.fma.R;
import com.gsysk.mapUtils.MapFunctions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.gsysk.mapUtils.*;
import com.gsysk.phoneUtils.*;

public class VehicleTrackerMapActivity extends ActionBarActivity {

	GoogleMap map = null;
	RouteFormulator routes = null;

	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
	 
	  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_tracking);


        try
        {
          routes = new RouteFormulator(PhoneFunctions.getFromPrivateSharedPreferences(this,"DropPointList"));


     // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);


            // Getting Map for the SupportMapFragment
            map = fm.getMap();

            Marker src = map.addMarker(new MarkerOptions().position(new LatLng(routes.getSource().latitude, routes.getSource().longitude)).title(routes.getSource().name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            Marker dst = map.addMarker(new MarkerOptions().position(new LatLng(routes.getDestination().latitude,routes.getDestination().longitude)).title(routes.getDestination().name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

            Marker [][] dropPoints = new Marker[routes.getNumberOfRoutes()][];

            for(int i=0;i<routes.getNumberOfRoutes();i++) {

                dropPoints[i] = new Marker[routes.getDropPoints()[i].length];
                for(int j=0;j<routes.getDropPoints()[i].length;j++)
                {
                    dropPoints[i][j] = map.addMarker(new MarkerOptions().position(new LatLng(routes.getDropPoints()[i][j].latitude, routes.getDropPoints()[i][j].longitude)).title(routes.getDropPoints()[i][j].name));
                }

            }





            final CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(routes.getSource().latitude, routes.getSource().longitude),10f);


          // Initializing
            // markerPoints = new ArrayList<LatLng>();
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
                    map.animateCamera(cu);
                }
            });


            MapFunctions.plotAllRoutes(this,map,routes);
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



       
	
}

