package com.gsysk.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gsysk.asynctasks.CloudParserAsyncTask;
import com.gsysk.asynctasks.DownloadAsyncTask;
import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.guiDisplays.NavigationDrawerFragmentUser;
import com.gsysk.mapUtils.MapFunctions;

import com.gsysk.phoneUtils.DriverLocationForUser;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MapActivityUser extends ActionBarActivity
        implements NavigationDrawerFragmentUser.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentUser mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DrawerLayout drawerLayout;
    //static final LatLng iiitb = new LatLng(12.844846, 77.663231);
    //
    //GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapActivityUser.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    Marker marker= null;
    //private static final String TAG = "BroadcastTest";
    private Intent intent;

    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_user);
        setUpMapIfNeeded();

        try
        {
            username = getIntent().getBundleExtra("DataBundle").getString("UserName");
        }
        catch(NullPointerException e)
        {
            username = PhoneFunctions.getFromPrivateSharedPreferences(MapActivityUser.this,"UserName");
        }

        mNavigationDrawerFragment = (NavigationDrawerFragmentUser)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        intent = new Intent(this, DriverLocationForUser.class);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        //setUpMap();
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapuser)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        Log.d("MyApp", "In setUpMap");

        mMap.setMyLocationEnabled(true);


        SharedPreferences prefs = getSharedPreferences("saveDetails",MODE_PRIVATE);
        String content = prefs.getString("vehicle_id_user",null);
        //Log.d("MyApp-got driverid to s",content);
        int v_id = Integer.parseInt(content);


        //get the current location from cloud here .
        ParseQuery<ParseObject> query = ParseQuery.getQuery("vehiclelocation");
        List<ParseObject> vehicleloc;
        ParseObject loc = new ParseObject("vehiclelocation");
        double latitude,longitude;
        query.whereEqualTo("vehicleid",v_id);
        try {
            vehicleloc = query.find();
            loc = vehicleloc.get(0);
            latitude= loc.getDouble("latitude");
            longitude = loc.getDouble("longitude");
            LatLng latLng = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vehicle Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.van_black)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        markroute();
    }

    @Override
    protected void onResume() {
        if (PhoneFunctions.getFromPrivateSharedPreferences(MapActivityUser.this,"savedUserName").equals("Empty"))
        {
            if(PhoneFunctions.getFromPrivateSharedPreferences(MapActivityUser.this,"savedPassword").equals("Empty"))
            {
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            startService(intent);

            registerReceiver(broadcastreceiver, new IntentFilter(DriverLocationForUser.BROADCAST_ACTION));
        }

        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

        try
        {
            unregisterReceiver(broadcastreceiver);
            stopService(intent);
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
                double latitude = loc.getDouble("LATITUDE");
                double longitude = loc.getDouble("LONGITUDE");
                Log.d("Myapp",String.valueOf(latitude));
            /*Toast.makeText(MapActivityUser.this,
                    "Triggered by Service!\n"
                            + "Data passed: Latitude : " + String.valueOf(latitude) + " Longitude : " + String.valueOf(longitude),
                    Toast.LENGTH_LONG).show();*/

                handleNewLocation(latitude,longitude);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    };

    private void handleNewLocation(double currentLatitude,double currentLongitude) {


        //storeLocationInCloud(currentLatitude,currentLongitude);

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);


        options = new MarkerOptions().position(latLng).title("Vehicle location").icon(BitmapDescriptorFactory.fromResource(R.drawable.van_black));
        if(marker!=null){
            marker.remove();
        }
        marker = mMap.addMarker(options);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mapuser, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1_for_user);
                break;
            case 2:
                mTitle = getString(R.string.title_section2_for_user);
                break;
            case 3:
                mTitle = getString(R.string.title_section3_for_user);
                break;
            case 4:
                mTitle = getString(R.string.title_section4_for_user);
                break;
            case 5:
                mTitle = getString(R.string.title_section5_for_user);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_user, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.action_refresh)
        {
            new CloudParserAsyncTask(MapActivityUser.this,ConstantValues.ROLE_USER+" : "+username,true).execute();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_user, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MapActivityUser) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void markroute() {


        double s_lat,d_lat,d_lang,s_lang;
        String s_name,d_name;


        String src = PhoneFunctions.getFromPrivateSharedPreferences(this, "SourceToDriverU");
        String d_points = PhoneFunctions.getFromPrivateSharedPreferences(this, "routeForDriverU");

        String src_data = src.split(" ; ")[0];
        String[] s_details= src_data.split(" : ");
        /*Log.d("MyApp-In markroute",src_data);
        for(String s : s_details){
            Log.d("MyApp-In markroute",s);
        }*/

        //Log.d("MyApp-In markroute",d_points);
        s_name = s_details[0];
        s_lat = Double.parseDouble(s_details[1]);
        s_lang = Double.parseDouble(s_details[2]);
        LatLng srcobj = new LatLng(s_lat,s_lang);

        MarkerOptions srcmarker = new MarkerOptions().position(srcobj).title(s_name).icon(BitmapDescriptorFactory.fromResource(R.drawable.star));
        Marker srcMarker1 = mMap.addMarker(srcmarker);
        //Log.d("MyApp-In markroute","Here2");
        String[] drpdetails = d_points.split(" ; ");

        LatLng[] drppoints = new LatLng[drpdetails.length];

        LatLng dpobj;

        MarkerOptions dpmarker;
        Marker dMarker;

        /*Log.d("MyApp-In markroute",d_points);
        for(String s : drpdetails){
            Log.d("MyApp-In markroute",s);
        }*/
        int i=0;
        for(String s : drpdetails){

            String[] d_details= s.split(" : ");
            d_name = d_details[0];
            d_lat = Double.parseDouble(d_details[1]);
            d_lang = Double.parseDouble(d_details[2]);

            dpobj = new LatLng(d_lat,d_lang);
            drppoints[i] = dpobj;
            i++;
            dpmarker = new MarkerOptions().position(dpobj).title(i+"."+d_name).icon(BitmapDescriptorFactory.fromResource(R.drawable.purplebusstop));
            dMarker = mMap.addMarker(dpmarker);
        }

        String url = MapFunctions.getDirectionsUrl(srcobj, drppoints);
        Log.d("MyApp-URL",url);
        DownloadAsyncTask downloadTask = new DownloadAsyncTask(mMap,this, ConstantValues.COLOR_ARRAY[1]);
        downloadTask.execute(url);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        String username = PhoneFunctions.getFromPrivateSharedPreferences(MapActivityUser.this,"savedUserName");
        String password = PhoneFunctions.getFromPrivateSharedPreferences(MapActivityUser.this,"savedPassword");
        if (!username.equals("Empty")&&!username.equals("Not Found"))
        {
            if(!password.equals("Empty")&&!password.equals("Not Found"))
            {
                moveTaskToBack(true);
            }
        }
        else
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        try
        {
            unregisterReceiver(broadcastreceiver);
            stopService(intent);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}
