package com.gsysk.guiDisplays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gsysk.fma.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.gsysk.phoneUtils.PhoneFunctions;
/**
 * Created by lenovo on 02-03-2015.
 */
public class AlertDialogHelper {

    Activity curActivity= null;
    String titleMessage = "";
    LinkedHashMap<Integer,String> mapIDtoName = null;

    public class dropPoints
    {
        public String name;
        public int id;
        public String contact;
        public int routeNum;
    }

    public dropPoints [] dropPointsList;
    public AlertDialogHelper(Activity activity,String title)
    {
        curActivity = activity;
        titleMessage = title;
    }
    public void createListAlertDialog(List <ParseObject> parseObjectList)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
        builder.setTitle(titleMessage);
        Dialog dialog = null;
        ListView modeList = new ListView(curActivity);
        String[] nameArray = new String[parseObjectList.size()];
        final String[] phoneNumArray = new String[parseObjectList.size()];


        for(int ind=0;ind<parseObjectList.size();ind++)
        {
            nameArray[ind] = " "+parseObjectList.get(ind).getString("Name");
            phoneNumArray[ind] = "  Number :"+parseObjectList.get(ind).getString("PhoneNumber");
        }

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<parseObjectList.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("mainTxt", nameArray[i]);
            hm.put("subTxt",phoneNumArray[i]);

            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "mainTxt","subTxt" };

        // Ids of views in listview_layout
        int[] to = { R.id.mainTxt,R.id.subTxt};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter modeAdapter = new SimpleAdapter(curActivity, aList, R.layout.listview_dialog_layout, from, to);

        int[] colors = {0, 0xFFFF0000, 0}; // red for the example
        modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        modeList.setDividerHeight(2);
        modeList.setAdapter(modeAdapter);

        // Item Click Listener for the listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
            PhoneFunctions phoneFunctions = new PhoneFunctions(curActivity);

                phoneFunctions.makePhoneCall(phoneNumArray,position);


            }
        };

        // Setting the item click listener for the listview
        modeList.setOnItemClickListener(itemClickListener);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(dialog!=null)
                {
                    dialog.dismiss();
                }
            }
        });
        builder.setView(modeList);
        dialog = builder.create();


        dialog.show();

    }

    public void createListAlertDialog(String cloudString, final String type)
    {
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
            builder.setTitle(titleMessage);
            Dialog dialog = null;
            ListView modeList = new ListView(curActivity);

            String []detailsArray = cloudString.split(" ; ");
            String[] nameArray = new String[detailsArray.length];
            final String[] phoneNumArray = new String[detailsArray.length];
            String[] routeArray = new String[detailsArray.length];
            String[] dropPointArray = new String[detailsArray.length];

            if(type.equals("Contact"))
            {
                mapIDtoName = createLookUp();


            }
            else if(type.equals("DropPoints"))
            {
                dropPointsList = createDPLookUp(detailsArray);
            }

            // Each row in the list stores country name, currency and flag
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            if(type.equals("Contact") || type.equals("Driver"))
            {
                for(int ind=0;ind<detailsArray.length;ind++)
                {
                    String [] parts = detailsArray[ind].split(" : ");
                    nameArray[ind] = " "+parts[0];
                    phoneNumArray[ind] = "  Number : "+parts[1];

                    if(type.equals("Driver"))
                    {
                        routeArray[ind] = " Route "+parts[2];
                    }
                    else if(type.equals("Contact"))
                    {
                        routeArray[ind] = " "+mapIDtoName.get(Integer.parseInt(parts[2]));
                    }

                }


                for(int i=0;i<detailsArray.length;i++){
                    HashMap<String, String> hm = new HashMap<String,String>();
                    hm.put("mainTxt", nameArray[i]);
                    hm.put("subTxt",phoneNumArray[i]);
                    hm.put("subTxt2",routeArray[i]);
                    aList.add(hm);
                }


            }
            else if(type.equals("DropPoints"))
            {
                for(int ind=0;ind<dropPointsList.length;ind++)
                {
                       dropPointArray[ind] = dropPointsList[ind].name;
                      routeArray[ind] = " Route "+dropPointsList[ind].routeNum;
                        nameArray[ind] = " Contact: "+dropPointsList[ind].contact;

                }



                for(int i=0;i<detailsArray.length;i++){
                    HashMap<String, String> hm = new HashMap<String,String>();
                    hm.put("mainTxt",  dropPointArray[i]);
                    hm.put("subTxt",routeArray[i]);
                    hm.put("subTxt2", nameArray[i]);
                    aList.add(hm);
                }


            }
            else if(type.equals("Routes"))
            {
                for(int ind=0;ind<detailsArray.length;ind++)
                {
                    String [] parts = detailsArray[ind].split(" : ");
                    nameArray[ind] = "  Driver :"+parts[0];
                    routeArray[ind] = " Route "+parts[2];



                }


                for(int i=0;i<detailsArray.length;i++){
                    HashMap<String, String> hm = new HashMap<String,String>();
                    hm.put("mainTxt", routeArray[i]);
                    hm.put("subTxt",nameArray[i]);

                    aList.add(hm);
                }
            }

            if(type.equals("Routes"))
            {
                // Keys used in Hashmap
                String[] from = { "mainTxt","subTxt"};

                // Ids of views in listview_layout
                int[] to = { R.id.mainTxt,R.id.subTxt};
                // Instantiating an adapter to store each items
                // R.layout.listview_layout defines the layout of each item
                SimpleAdapter modeAdapter = new SimpleAdapter(curActivity, aList, R.layout.listview_dialog_layout, from, to);

                int[] colors = {0, 0xFFFF0000, 0}; // red for the example
                modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
                modeList.setDividerHeight(2);
                modeList.setAdapter(modeAdapter);
            }
            else
            {
                // Keys used in Hashmap
                String[] from = { "mainTxt","subTxt","subTxt2" };

                // Ids of views in listview_layout
                int[] to = { R.id.mainTxt,R.id.subTxt,R.id.subTxt2};
                // Instantiating an adapter to store each items
                // R.layout.listview_layout defines the layout of each item
                SimpleAdapter modeAdapter = new SimpleAdapter(curActivity, aList, R.layout.listview_dialog_layout, from, to);

                int[] colors = {0, 0xFFFF0000, 0}; // red for the example
                modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
                modeList.setDividerHeight(2);
                modeList.setAdapter(modeAdapter);
            }



            // Item Click Listener for the listview
            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                    // Getting the Container Layout of the ListView
                    if(type.equals("Driver")||type.equals("User"))
                    {
                        PhoneFunctions phoneFunctions = new PhoneFunctions(curActivity);

                        phoneFunctions.makePhoneCall(phoneNumArray,position);
                    }



                }
            };

            // Setting the item click listener for the listview
            modeList.setOnItemClickListener(itemClickListener);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if(dialog!=null)
                    {
                        dialog.dismiss();
                    }
                }
            });
            builder.setView(modeList);
            dialog = builder.create();


            dialog.show();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private  dropPoints [] createDPLookUp( String[] detailsArray) {

        String []nameArray = new String[detailsArray.length];

        for(int ind=0;ind<detailsArray.length;ind++) {
            String[] parts = detailsArray[ind].split(" : ");
            nameArray[ind] = parts[0];

        }

        dropPoints[] temp = new dropPoints[detailsArray.length];

        LinkedHashMap<Integer,String> ContactList = createLookUp();

        int count  = 0;

        String [] clusterParts = PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"DropPointList").split(" # ");
        for(int i=0;i<clusterParts.length;i++)
            System.out.println("Cluster Points in List Contacts: "+clusterParts[i]);

        for(int i=0;i<clusterParts.length;i++)
        {
            String [] routeParts = clusterParts[i].split(" , ");

            for(int j=1;j<routeParts.length;j++)
            {
                String []dropPointParts = routeParts[j].split(" ; ");

                for(int k=0;k<dropPointParts.length;k++)
                {
                    if(k == dropPointParts.length -1 && j == routeParts.length-1)
                    {
                        continue;
                    }
                    System.out.println("Drop Point details : "+dropPointParts[k]);
                    String [] parts = dropPointParts[k].split(" / ");
                    temp[count] = new dropPoints();
                    temp[count].id = Integer.parseInt(parts[5]);
                    temp[count].name = parts[0];
                    temp[count].contact = getName(detailsArray,temp[count].id);
                    temp[count].routeNum = Integer.parseInt(parts[1]);

                    count++;

                }
            }
        }


        return temp;
    }

    private String getName(String[] detailsArray, int id) {

        String result ="";

        String []nameArray = new String[detailsArray.length];

        for(int ind=0;ind<detailsArray.length;ind++) {
            String[] parts = detailsArray[ind].split(" : ");
            if(parts[2].equals(String.valueOf(id)))
            {
                result = parts[0];
                break;
            }

        }

        return result;

    }

    private LinkedHashMap<Integer, String> createLookUp() {

        LinkedHashMap<Integer, String>  temp = new LinkedHashMap<>();

        String [] clusterParts = PhoneFunctions.getFromPrivateSharedPreferences(curActivity,"DropPointList").split(" # ");
        for(int i=0;i<clusterParts.length;i++)
            System.out.println("Cluster Points in List Contacts: "+clusterParts[i]);

        for(int i=0;i<clusterParts.length;i++)
        {
            String [] routeParts = clusterParts[i].split(" , ");

            for(int j=1;j<routeParts.length;j++)
            {
                String []dropPointParts = routeParts[j].split(" ; ");

                for(int k=0;k<dropPointParts.length;k++)
                {
                    if(k == dropPointParts.length -1 && j == routeParts.length-1)
                    {
                        continue;
                    }
                    System.out.println("Drop Point details : "+dropPointParts[k]);
                    String [] parts = dropPointParts[k].split(" / ");
                    temp.put(Integer.parseInt(parts[5]),parts[0]);
                }
            }
        }


        return temp;
    }

    public void createListAlertDialog(String cloudString)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
        builder.setTitle(titleMessage);
        Dialog dialog = null;
        ListView modeList = new ListView(curActivity);

        String []detailsArray = cloudString.split(" ; ");
        String[] nameArray = new String[detailsArray.length];
        final String[] phoneNumArray = new String[detailsArray.length];


        for(int ind=0;ind<detailsArray.length;ind++)
        {
            String [] parts = detailsArray[ind].split(" : ");
            nameArray[ind] = " "+parts[0];
            phoneNumArray[ind] = "  Number : "+parts[1];
        }

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<detailsArray.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("mainTxt", nameArray[i]);
            hm.put("subTxt",phoneNumArray[i]);

            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "mainTxt","subTxt" };

        // Ids of views in listview_layout
        int[] to = { R.id.mainTxt,R.id.subTxt};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter modeAdapter = new SimpleAdapter(curActivity, aList, R.layout.listview_dialog_layout, from, to);

        int[] colors = {0, 0xFFFF0000, 0}; // red for the example
        modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        modeList.setDividerHeight(2);
        modeList.setAdapter(modeAdapter);

        // Item Click Listener for the listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
                PhoneFunctions phoneFunctions = new PhoneFunctions(curActivity);

                phoneFunctions.makePhoneCall(phoneNumArray,position);


            }
        };

        // Setting the item click listener for the listview
        modeList.setOnItemClickListener(itemClickListener);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(dialog!=null)
                {
                    dialog.dismiss();
                }
            }
        });
        builder.setView(modeList);
        dialog = builder.create();


        dialog.show();

    }
}
