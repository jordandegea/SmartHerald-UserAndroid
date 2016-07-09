package com.sinenco.sharednews;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
//import com.parse.interceptors.ParseLogInterceptor;


import java.util.Date;

/**
 * Created by jordandegea on 23/06/16.
 */
public class App extends Application {

    private static final String PARSE_APPLICATION_ID = "com.sinenco.sharednews";
    private static final String PARSE_SERVER_URL = "http://dev.shared-news.com/parse/";

    private static App singleton;

    public static App getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        initializeParse();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private void initializeParse(){
        //Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                //.addNetworkInterceptor(new ParseLogInterceptor())
                .applicationId(PARSE_APPLICATION_ID)
                .clientKey(PARSE_APPLICATION_ID)
                .server(PARSE_SERVER_URL)
                .build()
        );

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        ParseACL.setDefaultACL(defaultACL, true);

        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        if ( ParseUser.getCurrentUser() != null ) {
            parseInstallation.put("user", ParseUser.getCurrentUser());
        }
        parseInstallation.put("lastConnection", new Date());
        parseInstallation.saveInBackground();
    }

}
