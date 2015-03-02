package com.gsysk.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.parser.DirectionsJSONParser;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends ActionBarActivity {

	GoogleMap map = null;
	ArrayList<LatLng> markerPoints;
	public ProgressDialog progressDialog=null;
	String locname = "";
	String [] coordinates;
	int numOfOtherLocs = -1;
	private Button showAll = null;
	private Marker [] others = null;
	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
	 
	  super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
       
        
        
        try
        {
       
        
     // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
 
        // Getting Map for the SupportMapFragment
        map = fm.getMap();
		/* GoogleMap googleMap = null;
	        try { 
	            if (googleMap == null) {
	               googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	            }
	         googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/
       
          float[] results = new float[1];
	   //     LatLng srcLocation = new LatLng(curLat,curLong);
	        LatLng dstLocation = new LatLng(12.991988,77.570892);
	        LatLng srcLocation = new LatLng(12.844940,77.663252);
	       // Location.distanceBetween(curLat, curLong,
	          //		   lat,longi, results);
	      
	        Marker src = map.addMarker(new MarkerOptions().position(srcLocation).title(ConstantValues.SOURCE));
	        Marker dst = map.addMarker(new MarkerOptions().position(dstLocation).title(ConstantValues.DESTINATION).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	      
	        
	        Location location = null;
           
	        
	      //  map.getUiSettings().setZoomGesturesEnabled(true);
	       // } catch (Exception e) {
	       //     e.printStackTrace();
	       //  }
	        
	      
        
 
        if(map!=null){
 
            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);
     
         // Getting LocationManager object from System Service LOCATION_SERVICE
	        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	
	        // Creating a criteria object to retrieve provider
	        Criteria criteria = new Criteria();
	
	        // Getting the name of the best provider
	        String provider = locationManager.getBestProvider(criteria, true);
	
	        // Getting Current Location From GPS
	       location = getLastBestLocation();
	        
	      // String newLat = getFromSharedPreferences("NewLat");
	       //String newLong = getFromSharedPreferences("NewLong");
	       
	       
	     /*  LatLng srcLocation = null;
	       if((newLat == null || newLat.equals("")&&(newLong == null || newLong.equals(""))))
	       {
	    	   srcLocation = new LatLng(location.getLatitude(),location.getLongitude());
	       }
	       else
	       {
	    	   srcLocation = new LatLng(Double.valueOf(newLat), Double.valueOf(newLong));
	       }
       
        Marker src = map.addMarker(new MarkerOptions().position(srcLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));;
        */ //   locationManager.requestLocationUpdates(provider, 20000, 0, this);
            // Setting onclick event listener for the map
          //  map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
 
               // @Override
              //  public void onMapClick(LatLng point) {
 
                 /*   // Already two locations
                    if(markerPoints.size()>1){
                        markerPoints.clear();
                        map.clear();
                    }
 
                    // Adding new item to the ArrayList
                    markerPoints.add(point);
 
                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();
 
                    // Setting the position of the marker
                    options.position(point);
 
                    /**
                    * For the start location, the color of marker is GREEN and
                    * for the end location, the color of marker is RED.
                    */
                  /*  if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
 
                    // Add new marker to the Google Map Android API V2
                    map.addMarker(options);
 
                    // Checks, whether start and end locations are captured
                    if(markerPoints.size() >= 2){*/
                      //  LatLng origin = markerPoints.get(0);
                       // LatLng dest = markerPoints.get(1);
 
                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(srcLocation, dstLocation);
 
                        DownloadTask downloadTask = new DownloadTask();
 
                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                        
                        //To zoom into a map
            	        Marker[] markers = new Marker[2];
            	        markers[0] = src;
            	        markers[1] = dst;
            	        LatLngBounds.Builder builder = new LatLngBounds.Builder();
            	        
            	        for (Marker marker : markers) {
            	            builder.include(marker.getPosition());
            	        }
                
            	        final LatLngBounds bounds = builder.build();
            	        int padding = 0; // offset from edges of the map in pixels
            	     //   final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            	        final CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(srcLocation.latitude, srcLocation.longitude), 10.9f);
            	     
                 // Initializing
                  // markerPoints = new ArrayList<LatLng>();
            	        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            	        	@Override
            	        	public void onMapLoaded() {
            	        		//map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
            	        		map.animateCamera(cu);
            	        	}
            	        });
                    }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
             //   }
          //  });
      //  }
    }
 
    private String getDirectionsUrl(LatLng origin,LatLng dest){
 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            
            try
            {
            	// Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();
     
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
     
                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);
     
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
     
                        points.add(position);
                    }
     
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);
                }
     
                // Drawing polyline in the Google Map for the i-th route
                map.addPolyline(lineOptions);
            }
            catch(NullPointerException e)
            {
            	e.printStackTrace();
            	 
            	Toast.makeText(getApplicationContext(), "It looks like your internet is down. Please go back, enable internet, and try again.", Toast.LENGTH_LONG).show();
            }
            
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Location getLastBestLocation() {
        LocationManager mLocationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        Location locationGPS = null;
        Location locationNet = null;

       	locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
          //  	Toast.makeText(getApplicationContext(), "Latitude : "+locationGPS.getLatitude()+"\nLongitude : "+locationGPS.getLongitude(), Toast.LENGTH_LONG).show();
        	return locationGPS;
        }
        else {
        	//Toast.makeText(getApplicationContext(), "Latitude : "+locationNet.getLatitude()+"\nLongitude : "+locationNet.getLongitude(), Toast.LENGTH_LONG).show();
            return locationNet;
        }
    }
    
    public float getDistance(double lat1, double lon1, double lat2, double lon2) {
        String result_in_kms = "";
        String url = "http://maps.google.com/maps/api/directions/xml?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric";
        String tag[] = {"text"};
        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream is = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms =String.valueOf( args.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Float f = 0.0f;
        try
        {
        	 f=Float.valueOf(result_in_kms);
        }
        catch(Exception e)
        {
        	if(result_in_kms.endsWith(" km"))
        	{
        		result_in_kms.substring(0,result_in_kms.length()-3);
        	}
        }
        return f*1000;
    }
    
    public void updateInSharedPreferences(String key, String value)
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	prefs.edit().putString(key, value).commit();
    }
    
    public String getFromSharedPreferences(String key)
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	
    	String value=prefs.getString(key, "");
    	
    	return value;
    }
       
	
}

