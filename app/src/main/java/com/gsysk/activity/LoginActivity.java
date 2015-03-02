package com.gsysk.activity;



import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gsysk.guiDisplays.ToastMessageHelper;
import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.phoneUtils.PhoneFunctions;


public class LoginActivity extends ActionBarActivity {

	private Button loginbtn = null;
	private Button clearbtn = null;
	private EditText username = null;
	private EditText password = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       loginbtn = (Button)findViewById(R.id.loginbtn);
        clearbtn = (Button)findViewById(R.id.clrBtn);
        
        username = (EditText)findViewById(R.id.usernameVal);
        password = (EditText)findViewById(R.id.passwordVal);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(PhoneFunctions.isInternetEnabled(LoginActivity.this))
                {
                    Intent adminBtnActIntent = new Intent(getApplicationContext(),AdminButtonActivity.class);
                    startActivity(adminBtnActIntent);
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
