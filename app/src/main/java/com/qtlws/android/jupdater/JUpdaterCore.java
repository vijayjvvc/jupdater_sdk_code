package com.qtlws.android.jupdater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class JUpdaterCore extends JUpdater {


    private static JUpdaterCore var1xi;

    public static final String SDKNAME = BuildConfig.SDK_NAME;
    public static final String VERSION = BuildConfig.VERSION;
    public static final String BUILDNUMBER = BuildConfig.BUILDNUMBER;

    private static JThread var5tdx;

    private static Context var2xct;
    private static Context var2;
    private static JUpdaterConfig config;
    private boolean var111x = true;



    static {
        var1xi = new JUpdaterCore();
    }


    @Override
    public void init(@NonNull Context context, JUpdaterConfig updaterConfig) {
        try {

            if (updaterConfig.getUpdatedApkUrl() == null || updaterConfig.getUpdatedApkUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("Update APK URL is missing or empty. SDK initialization failed.");
            }
            var2 = context;
            var2xct = context.getApplicationContext();
            config = updaterConfig;
            JLogger.var36dgx = config.isDebug();
            JLogger.infoLog("Init Successfully");
            JLogger.infoLog(String.format("Starting JUpdater Service: (v%s.%s)", VERSION, BUILDNUMBER));
            JLogger.infoLog(("Build Number: " + BUILDNUMBER));

            if (JLifecycleListener.listener == null) {
                JLifecycleListener.$$b(var2xct, new JLifecycleListener.ForegroundListener() {
                    @Override
                    public void onAppForeground(Activity var1) {
                        JLogger.infoLog("onBecameForeground");
                        startService();
                    }

                    @Override
                    public void onAppBackground(Context var1) {
                        JLogger.infoLog("onBecameBackground");
                    }
                });
            }

        } catch (Exception e) {
            JLogger.errorLog("[Error] While init " + e.getLocalizedMessage());
        }


    }

    @Override
    public void launchUrlInBrowser(@NonNull Context context, @NonNull String urlToOpen) {
        try{

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        JLogger.infoLog("Launching :- " + urlToOpen);
        context.startActivity(intent);
        }catch (Exception e){
            JLogger.errorLog(e);
        }
    }


    private void startService() {
        if (var2 instanceof Application) {
            JLogger.debugLog("Application context is found");
        } else if (var2 instanceof Activity) {
            JLogger.debugLog("Activity context is found");
        } else {
            JLogger.debugLog("Unknown context is found");
        }
        JLogger.debugLog("JUpdater initialized with URL: " + config.getUpdatedApkUrl());
        var5tdx = new JThread(() -> {
            UpdateChecker.checkForUpdate(var2xct, config);
        });
        var5tdx.start();
    }

    @Override
    protected void launchUpdateScreen(@NonNull boolean isForceUpdate) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isForceUpdate) {
                    if (var111x) {
                        Intent intent = new Intent(var2xct, UpdateBlocking.class);
                        intent.putExtra("update_url", config.getUpdatedApkUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        var2xct.startActivity(intent);
                        var111x = false;
                    }else {
                        if(JLifecycleListener.getCurrentActivity() instanceof UpdateBlocking){
                            JLogger.debugLog("OK_111x");
                        }else {
                            var111x = true;
                            launchUpdateScreen(true);
                        }
                    }
                } else {
                    JUpdateSheetController.launch(config);
                }
            });
            if (var5tdx != null) {
                var5tdx.interrupt();
                var5tdx = null;
            }
        } catch (Exception e) {
            JLogger.errorLog("[Error] While Closing Thread " + e.getLocalizedMessage());
        }

    }

    public static JUpdater getInstance() {
        return var1xi;
    }


}
