package com.gsysk.activity;


import com.gsysk.guiDisplays.AlertDialogHelper;
import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.asynctasks.CloudParserAsyncTask;
import com.gsysk.constants.ConstantValues;


import java.util.List;


import com.gsysk.fma.R;
import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.phoneUtils.PhoneFunctions;

import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;

import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;



public class AdminButtonActivity extends ActionBarActivity {

	private Button listDrivers = null;
	private Button listContacts = null;
	private Button trackVehicles = null;
    private String username="";
    private Button viewRoutes = null;
    public CloudInteractor pullAllService = null;
	
	private ProgressDialog progressDialog = null;
	List<ParseObject> parseObjectList = null;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_button);


        username = getIntent().getBundleExtra("DataBundle").getString("UserName");


        listDrivers = (Button)findViewById(R.id.row1col1);
        listContacts = (Button)findViewById(R.id.row3col3);
        trackVehicles = (Button)findViewById(R.id.row2col3);
        viewRoutes = (Button)findViewById(R.id.row2col2);

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
                    createdDialog.createListAlertDialog(content);
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
                    createdDialog.createListAlertDialog(content);
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
            new CloudParserAsyncTask(AdminButtonActivity.this,"admin"+" : "+username).execute();
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



    }
}
