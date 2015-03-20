package com.gsysk.mapUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gsysk.asynctasks.DownloadAsyncTask;
import com.gsysk.asynctasks.ParserAsyncTask;
import com.gsysk.constants.ConstantValues;
import com.gsysk.parser.DirectionsJSONParser;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by lenovo on 04-03-2015.
 */
public class MapFunctions {

    public static String getDirectionsUrl(LatLng origin,LatLng dest){

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

    public static String getDirectionsUrl(LatLng source, LatLng [] points){

       /*String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;
*/
        // Origin of route
        String str_origin = "origin="+source.latitude+","+source.longitude;

        // Destination of route
        String str_dest = "destination="+points[points.length-1].latitude+","+points[points.length-1].longitude;
        String waypoints = "waypoints=optimize:true";

        for(int i=0;i<points.length-1;i++)
        {
            System.out.println(i+ " : "+points[i].latitude+" "+points[i].longitude);
            waypoints = waypoints.concat("|"+points[i].latitude+","+points[i].longitude);
        }


        // Sensor enabled
        String sensor = "sensor=false";



        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+ waypoints+"&"+sensor;


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    public static String downloadUrl(String strUrl) throws IOException {
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






    public static void plotAllRoutes(final Activity curActivity, final GoogleMap map,RouteFormulator [] routes)
    {

        int numRoutesPlotted = 0;
        for(int k=0;k<routes.length;k++)
        {
            LatLng source = new LatLng(routes[k].getSource().latitude,routes[k].getSource().longitude);
            //    LatLng destination = new LatLng(routes.getDestination().latitude,routes.getDestination().longitude);

            int numberOfRoutes = routes[k].getNumberOfRoutes();

            LatLng dropPoints[][] = new LatLng[numberOfRoutes][];

            for(int i=0;i<numberOfRoutes;i++)
            {
                dropPoints[i] = new LatLng[routes[k].getDropPoints()[i].length];
                for(int j=0;j<routes[k].getDropPoints()[i].length;j++)
                {
                    dropPoints[i][j] = new LatLng(routes[k].getDropPoints()[i][j].latitude,routes[k].getDropPoints()[i][j].longitude);
                }
            }




            if(map!=null){

                // Enable MyLocation Button in the Map
                map.setMyLocationEnabled(true);

                // Getting LocationManager object from System Service LOCATION_SERVICE
                LocationManager locationManager = (LocationManager) curActivity.getSystemService(Context.LOCATION_SERVICE);

                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();

                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);

                for(int i=0;i<numberOfRoutes;i++)
                {
                   /* String url = MapFunctions.getDirectionsUrl(source, dropPoints[i][0]);

                    DownloadAsyncTask downloadTask = new DownloadAsyncTask(map,curActivity,ConstantValues.COLOR_ARRAY[numRoutesPlotted+i]);

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    int j=0;
                    for(j=0;j<dropPoints[i].length-1;j++)
                    {
                        String url2 = MapFunctions.getDirectionsUrl(dropPoints[i][j],dropPoints[i][j+1]);

                        DownloadAsyncTask downloadTask2 = new DownloadAsyncTask(map,curActivity,ConstantValues.COLOR_ARRAY[numRoutesPlotted+i]);

                        // Start downloading json data from Google Directions API
                        downloadTask2.execute(url2);
                    }

                    //  String url3 = MapFunctions.getDirectionsUrl(dropPoints[i][j], destination);

                    //  DownloadAsyncTask downloadTask3 = new DownloadAsyncTask(map,ConstantValues.COLOR_ARRAY[i]);

                    // Start downloading json data from Google Directions API
                    // downloadTask3.execute(url3);
*/

                    String url = MapFunctions.getDirectionsUrl(source,dropPoints[i]);
                    DownloadAsyncTask downloadTask = new DownloadAsyncTask(map,curActivity,ConstantValues.COLOR_ARRAY[numRoutesPlotted++]);
                    downloadTask.execute(url);

                }




            }
        }

    }


    public static Location getLastBestLocation(Activity activity) {
        LocationManager mLocationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
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



}
