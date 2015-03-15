package com.gsysk.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.gsysk.mapUtils.MapFunctions;
import com.gsysk.parser.DirectionsJSONParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 04-03-2015.
 */
// Fetches data from url passed
public class DownloadAsyncTask extends AsyncTask<String, Void, String> {

    GoogleMap map = null;
    int color = -1;
    Activity curActivity;

    public DownloadAsyncTask(GoogleMap map,Activity activity, int color)
    {
        this.map = map;
        this.color = color;
        curActivity = activity;
    }
    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            data = MapFunctions.downloadUrl(url[0]);
        }catch(Exception e){
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserAsyncTask parserTask = new ParserAsyncTask(map,curActivity,color);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);
    }
}

