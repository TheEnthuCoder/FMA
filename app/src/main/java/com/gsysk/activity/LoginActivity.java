package com.gsysk.activity;


import com.gsysk.asynctasks.CloudParserAsyncTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.parseCloudServices.CloudInteractor;
import com.gsysk.phoneUtils.PhoneFunctions;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

	private Button loginbtn = null;
	private Button clearbtn = null;
	private EditText username = null;
	private EditText password = null;

    private CheckBox remember = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Parse.initialize(getApplicationContext(), ConstantValues.APP_KEY, ConstantValues.CLIENT_KEY);
        //      Parse.initialize(curActivity, "XdzcMtL72Ho3GmVBbCaEY7pzdg8cGXF1EkyTbdUw", "mpsFnnEuQURv0KzH4dPy0xtV8vN8gZdRTSzCDoix");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

       loginbtn = (Button)findViewById(R.id.loginbtn);
        clearbtn = (Button)findViewById(R.id.clrBtn);
        
        username = (EditText)findViewById(R.id.usernameVal);
        password = (EditText)findViewById(R.id.passwordVal);

        remember = (CheckBox)findViewById(R.id.checkforremember);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().equals("") && password.getText().toString().equals(""))
                {
                    ToastMessageHelper.displayShortToast(LoginActivity.this,"Username and password fields cannot be blank");
                }
                else if(username.getText().toString().equals(""))
                {
                    ToastMessageHelper.displayShortToast(LoginActivity.this,"Username field cannot be blank");
                }
                else if(password.getText().toString().equals(""))
                {
                    ToastMessageHelper.displayShortToast(LoginActivity.this,"Password field cannot be blank");
                }
                else if(PhoneFunctions.isInternetEnabled(LoginActivity.this))
                {
                    if(remember.isChecked())
                    {
                        PhoneFunctions.storeInPrivateSharedPreferences(LoginActivity.this,"savedUserName",username.getText().toString());
                        PhoneFunctions.storeInPrivateSharedPreferences(LoginActivity.this,"savedPassword",password.getText().toString());
                    }

                    new CloudParserAsyncTask(LoginActivity.this,username.getText().toString(),password.getText().toString()).execute();
                }
                else
                {
                    ToastMessageHelper.displayLongToast(LoginActivity.this,ConstantValues.ENABLE_INTERNET);
                }

            }
        });
        
        clearbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				username.setText(ConstantValues.BLANK);
				password.setText(ConstantValues.BLANK);
				
			}
		});
    }

    @Override
    protected void onResume() {

        String username = PhoneFunctions.getFromPrivateSharedPreferences(LoginActivity.this,"savedUserName");
        String password = PhoneFunctions.getFromPrivateSharedPreferences(LoginActivity.this,"savedPassword");
        if (username!=null&&!username.equals("Not Found")&&!username.equals("Empty"))
        {
            if(password!=null&&!password.equals("Not Found")&&!password.equals("Empty"))
            {
                Intent intent = new Intent(this,AdminButtonActivity.class);
                startActivity(intent);
            }
        }




        super.onResume();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
