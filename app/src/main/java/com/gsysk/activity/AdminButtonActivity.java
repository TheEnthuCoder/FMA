package com.gsysk.activity;


import com.gsysk.guiDisplays.AlertDialogHelper;
import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.asynctasks.CloudParserAsyncTask;
import com.gsysk.constants.ConstantValues;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


import com.gsysk.fma.R;
import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.SaveCallback;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class AdminButtonActivity extends ActionBarActivity {

	private Button listDrivers = null;
	private Button listContacts = null;
	private Button trackVehicles = null;
    private Button listDropPoints = null;
    private String username="";
    private Button listRoutes = null;
    private Button viewRoutes = null;
    private Button reset = null;
    public CloudInteractor pullAllService = null;

    private IntentFilter intentFilter = null;
	
	private ProgressDialog progressDialog = null;
	List<ParseObject> parseObjectList = null;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_button);

        ParsePush.subscribeInBackground("CHANNEL_5", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        intentFilter = new IntentFilter();
        intentFilter.addAction("START_REFRESH");


        username = getIntent().getBundleExtra("DataBundle").getString("UserName");

        PhoneFunctions.storeInPrivateSharedPreferences(AdminButtonActivity.this,"curLogin",username);

        listDrivers = (Button)findViewById(R.id.row1col1);
        listContacts = (Button)findViewById(R.id.row3col3);
        listDropPoints = (Button)findViewById(R.id.row1col2);
        trackVehicles = (Button)findViewById(R.id.row2col3);
        listRoutes = (Button)findViewById(R.id.row1col3);
        viewRoutes = (Button)findViewById(R.id.row2col2);
        reset = (Button)findViewById(R.id.row3col2);

		pullAllService = new CloudInteractor(AdminButtonActivity.this);
        pullAllService.initialize(ConstantValues.APP_KEY,ConstantValues.CLIENT_KEY);

        listDrivers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                String content = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"ListOfDrivers");
                if(content==null || content.equals("Not Found"))
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this,ConstantValues.PLEASE_TRY_AGAIN);
                }
                else
                {
                    String titleMessage = "";

                    titleMessage = "List of Drivers : ";


                    AlertDialogHelper createdDialog = new AlertDialogHelper(AdminButtonActivity.this, titleMessage);
                    createdDialog.createListAlertDialog(content,"Driver");
                }

			}
		});
        
 	listContacts.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                String content = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"ListOfContacts");
                if(content==null || content.equals("Not Found"))
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this,ConstantValues.PLEASE_TRY_AGAIN);
                }
                else
                {
                    String titleMessage = "";

                    titleMessage = "List of Contacts : ";


                    AlertDialogHelper createdDialog = new AlertDialogHelper(AdminButtonActivity.this, titleMessage);
                    createdDialog.createListAlertDialog(content,"Contact");
                }


			}
		});


        listDropPoints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String content = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"ListOfContacts");
                if(content==null || content.equals("Not Found"))
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this,ConstantValues.PLEASE_TRY_AGAIN);
                }
                else
                {
                    String titleMessage = "";

                    titleMessage = "List of Drop Points : ";


                    AlertDialogHelper createdDialog = new AlertDialogHelper(AdminButtonActivity.this, titleMessage);
                    createdDialog.createListAlertDialog(content,"DropPoints");
                }


            }
        });
        listRoutes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String content = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"ListOfDrivers");
                if(content==null || content.equals("Not Found"))
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this,ConstantValues.PLEASE_TRY_AGAIN);
                }
                else
                {
                    String titleMessage = "";

                    titleMessage = "List of Routes : ";


                    AlertDialogHelper createdDialog = new AlertDialogHelper(AdminButtonActivity.this, titleMessage);
                    createdDialog.createListAlertDialog(content,"Routes");
                }


            }
        });
 	trackVehicles.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
            if(PhoneFunctions.isInternetEnabled(AdminButtonActivity.this))
            {
                Intent intent = new Intent(getApplicationContext(),VehicleTrackerMapActivity.class);
                startActivity(intent);
            }
            else
            {
                ToastMessageHelper.displayLongToast(AdminButtonActivity.this, ConstantValues.ENABLE_INTERNET);
            }

		}
	});

        viewRoutes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(PhoneFunctions.isInternetEnabled(AdminButtonActivity.this))
                {
                    Intent intent = new Intent(getApplicationContext(),RoutesMapActivity.class);
                    startActivity(intent);
                }
                else
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this, ConstantValues.ENABLE_INTERNET);
                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<String> droppointIds = new ArrayList<String>();

                String [] clusterParts = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"DropPointList").split(" # ");
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
                            droppointIds.add(parts[5]);



                        }
                    }
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(AdminButtonActivity.this);
                alert.setTitle("Reset Status");
                alert.setMessage("This will reset the delivery statuses at all drop points. Are you sure you want to proceed?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ResetStatusAsyncTask(AdminButtonActivity.this,droppointIds).execute();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.create().show();


            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_button, menu);
        return true;
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
            new CloudParserAsyncTask(AdminButtonActivity.this,"admin"+" : "+username,true).execute();
        }
        else if(id == R.id.action_logout)
        {
            PhoneFunctions.storeInPrivateSharedPreferences(this,"savedUsername","Empty");
            PhoneFunctions.storeInPrivateSharedPreferences(this,"savedPassword","Empty");

            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        if (PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"savedUserName").equals("Empty"))
        {
            if(PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"savedPassword").equals("Empty"))
            {
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            registerReceiver(mReceiver, intentFilter);
        }




        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        String username = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"savedUserName");
        String password = PhoneFunctions.getFromPrivateSharedPreferences(AdminButtonActivity.this,"savedPassword");
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
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("START_REFRESH"))
            {
                new CloudParserAsyncTask(AdminButtonActivity.this,"admin"+" : "+username,false).execute();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        try
        {
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try
        {
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
