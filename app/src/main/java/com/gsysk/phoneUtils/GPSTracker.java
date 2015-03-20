package com.gsysk.phoneUtils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by SatishPK on 3/16/2015.
 */

public class GPSTracker extends Service implements LocationListener {

    private Context mContext=null;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    private static double latitude; // latitude
    private static double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private MarkerOptions options;
    Marker marker= null;
    private GoogleMap mMap;
    public static final String BROADCAST_ACTION  = "MY_ACTION";
    Intent intent;
    private final Handler handler = new Handler();



    public GPSTracker() {
        Log.d("MyApp","In const with context");


    }

    public Location getLocation() {
        Log.d("MyApp","In getLocation");

        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d("MyApp",String.valueOf(isGPSEnabled));
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //Toast.makeText(getApplicationContext(),"In service : Latitude : "+latitude+" Longitude : "+longitude,Toast.LENGTH_LONG).show();
                            Log.d("MyApp", "Network");
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d("MyApp", "GPS Enabled");

                           //     Toast.makeText(getApplicationContext(),"In service : Latitude : "+latitude+" Longitude : "+longitude,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                storeLocationInCloud(latitude,longitude);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void EnableGPS() {
        Log.d("MyApp","EnableGPS");

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }



    private void markonmap(double currentLatitude,double currentLongitude) {
        Log.d("MyApp","In markonMAp");
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        options = new MarkerOptions().position(latLng).title("I am here!");
        if(marker!=null){
            marker.remove();
        }
        marker = mMap.addMarker(options);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

    }

    private void storeLocationInCloud(double currentLatitude, double currentLongitude) {
        Log.d("MyApp",String.valueOf(currentLatitude));
        /*Intent intent = new Intent();
        Bundle loc = new Bundle();
        loc.putDouble("LATITUDE",currentLatitude);
        loc.putDouble("LONGITUDE",currentLongitude);
        intent.setAction(MY_ACTION);

        intent.putExtra("LOC",loc);

        sendBroadcast(intent);*/

        Log.d("MyApp","In storeInCloud");
        //Get vehicle id from cloud
        int vehicleid =1;

        /*ParseObject gameScore = new ParseObject("vehiclelocation");
        gameScore.put("vehicleid",1);
        gameScore.put("latitude", currentLatitude);
        gameScore.put("longitude", currentLongitude);
        gameScore.saveInBackground();*/



        ParseQuery<ParseObject> query = ParseQuery.getQuery("vehiclelocation");
        List<ParseObject> vehicleloc;
        ParseObject loc;// = new ParseObject("vehiclelocation");
        double latitude,longitude;
        query.whereEqualTo("vehicleid",1);
        try {
            vehicleloc = query.find();
            loc = vehicleloc.get(0);
            loc.put("latitude", currentLatitude);
            loc.put("longitude", currentLongitude);
            loc.saveInBackground();


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApp","In service oncreate");
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("MyApp","In service onstart");
        //Call getLocation on onCreate()
        getLocation();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 10000); // 10 second

    }




    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.d("MyApp","In service run");

            Bundle loc = new Bundle();
            loc.putDouble("LATITUDE",latitude);
            loc.putDouble("LONGITUDE",longitude);
            intent.setAction(BROADCAST_ACTION);

            intent.putExtra("LOC",loc);

            sendBroadcast(intent);

            //Call getLocation again to fetch next location update, thus, repeatedly calling getLocation
            getLocation();
            handler.postDelayed(this, 10000); // 10 seconds
        }
    };
}