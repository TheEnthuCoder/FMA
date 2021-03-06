package com.gsysk.asynctasks;

/**
 * Created by lenovo on 04-03-2015.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gsysk.parser.DirectionsJSONParser;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.Parse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** A class to parse the Google Places in JSON format */
public class ParserAsyncTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

    // Parsing the data in non-ui thread
    GoogleMap map = null;
    int color = -1;
    Activity curActivity;
    public ParserAsyncTask(GoogleMap map,Activity activity,int color)
    {
        this.map =map;
        this.color = color;
        curActivity = activity;
    }
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {

        drawRoute(result);
    }

    private boolean drawRoute(List<List<HashMap<String, String>>> result)
    {
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
                lineOptions.width(7);
                lineOptions.color(color);
            }

            // Drawing polyline in the Google Map for the i-th route
         /*   if(lineOptions == null)
            {
                System.out.println("Line options is null.. retracking");

                return false;
            }

            else */
                System.out.println("Line options is not null");
            map.addPolyline(lineOptions);
            int numRoutesDrawn = Integer.parseInt(PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"NumOfRoutesDrawn"));
            PhoneFunctions.storeInPrivateSharedPreferences(curActivity,"NumOfRoutesDrawn",""+(numRoutesDrawn+1));
        }
        catch(Exception e)
        {
            e.printStackTrace();


        }

        return true;
    }
}
