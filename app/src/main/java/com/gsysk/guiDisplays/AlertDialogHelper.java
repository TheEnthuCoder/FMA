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
import java.util.List;

import com.gsysk.phoneUtils.PhoneFunctions;
/**
 * Created by lenovo on 02-03-2015.
 */
public class AlertDialogHelper {

    Activity curActivity= null;
    String titleMessage = "";
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
}
