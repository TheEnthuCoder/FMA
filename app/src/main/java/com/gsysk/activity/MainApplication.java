package com.gsysk.activity;

import android.app.Application;

import com.gsysk.constants.ConstantValues;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Created by lenovo on 13-04-2015.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, ConstantValues.APP_KEY, ConstantValues.CLIENT_KEY);
        PushService.setDefaultPushCallback(this, LoginActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}