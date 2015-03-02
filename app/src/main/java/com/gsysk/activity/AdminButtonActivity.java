package com.gsysk.activity;


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
	private Button showMap = null;
    public CloudInteractor pullAllService = null;
	
	private ProgressDialog progressDialog = null;
	List<ParseObject> parseObjectList = null;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_button);
        
        listDrivers = (Button)findViewById(R.id.row1col1);
        listContacts = (Button)findViewById(R.id.row3col3);
        showMap = (Button)findViewById(R.id.row2col3);

		pullAllService = new CloudInteractor(AdminButtonActivity.this);
        pullAllService.initialize(ConstantValues.APP_KEY,ConstantValues.CLIENT_KEY);

        listDrivers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                if(PhoneFunctions.isInternetEnabled(AdminButtonActivity.this))
                {
                    new CloudParserAsyncTask(ConstantValues.DRIVERS,AdminButtonActivity.this,parseObjectList,pullAllService).execute();
                }
                else
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this, ConstantValues.ENABLE_INTERNET);
                }

			}
		});
        
 	listContacts.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                if(PhoneFunctions.isInternetEnabled(AdminButtonActivity.this))
                {
                    new CloudParserAsyncTask(ConstantValues.USERS,AdminButtonActivity.this,parseObjectList,pullAllService).execute();
                }
                else
                {
                    ToastMessageHelper.displayLongToast(AdminButtonActivity.this, ConstantValues.ENABLE_INTERNET);
                }

			}
		});
 	
 	showMap.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
            if(PhoneFunctions.isInternetEnabled(AdminButtonActivity.this))
            {
                Intent intent = new Intent(getApplicationContext(),MapActivity.class);
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

        return super.onOptionsItemSelected(item);
    }
}
