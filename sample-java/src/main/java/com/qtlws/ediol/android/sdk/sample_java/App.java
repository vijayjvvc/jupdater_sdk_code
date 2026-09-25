/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.ediol.android.sdk.sample_java;

import android.app.Application;

import com.qtlws.android.jupdater.JUpdater;
import com.qtlws.android.jupdater.JUpdaterConfig;

public class App extends Application {
    private JUpdater jUpdater = JUpdater.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        // For managed Server, get the api key from the jupdater console
        JUpdaterConfig configForManagedServer = JUpdaterConfig.useManagedServer("enter_your_api_key");
        //For production mode set it false  or just remove the line
        configForManagedServer.setDebug(true);
        jUpdater.init(this,configForManagedServer);

        // For Custom Server, get the backend live which support get request from the sdk to response with proper json data
        // JUpdaterConfig configForUserServer = JUpdaterConfig.useCustomServer("fallback_updated_app_url","enter_your_server_url");
        //For production mode set it false  or just remove the line
        // configForUserServer.setDebug(true);
        // jUpdater.init(this,configForUserServer);
    }

}

